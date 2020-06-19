package com.brokoli.rules

import com.android.tools.lint.detector.api.*
import com.android.tools.lint.detector.api.interprocedural.*
import org.jetbrains.uast.UMethod
import java.util.stream.Collectors

@Suppress("UnstableApiUsage")
class MoreThanOneOkHttpClientCallGraphDetector : Detector(), SourceCodeScanner {

    override fun isCallGraphRequired(): Boolean {
        return true
    }

    // Another call graph example: https://groups.google.com/forum/#!searchin/lint-dev/caller|sort:date/lint-dev/wFvCZOt4wZ8/g5punP99BAAJ
    override fun analyzeCallGraph(context: Context, callGraph: CallGraphResult) {
        val contextualCallGraph = callGraph.callGraph.buildContextualCallGraph(callGraph.receiverEval)
        val okHttpClientNodes = contextualCallGraph.contextualNodes.stream().filter { contextualNode ->
            val element = contextualNode.node.target.element
            if(element is UMethod) {
                element.containingClass?.qualifiedName == OKHTTP_CLIENT && element.name == "OkHttpClient"
            } else {
                false
            }
        }.collect(Collectors.toList())
        if(okHttpClientNodes.size == 0) {
            // OkHttpClient not used in this project
            return
        }
        if(okHttpClientNodes.size != 1) {
            val parser = context.client.getUastParser(context.project)
            val node = okHttpClientNodes.first()
            val location = parser.createLocation(node.node.target.element)
            reportUsage(context, location)
            return
        }
        backwardSearch(context, callGraph.callGraph, contextualCallGraph, okHttpClientNodes.first())
    }

    private fun backwardSearch(context: Context, callGraph: CallGraph, contextualCallGraph: ContextualCallGraph, node: ContextualNode) {
        val callers = contextualCallGraph.inEdges(node)
        if(callers.isEmpty()) {
            // We are done
            return
        }
        if(callers.size == 1) {
            backwardSearch(context, callGraph, contextualCallGraph, callers.first().contextualNode)
            return
        }
        val parser = context.client.getUastParser(context.project)
        callers.map { it.contextualNode }.forEach { contextualNode ->
            val location = parser.createLocation(contextualNode.node.target.element)
            reportUsage(context, location)
        }
    }

    private fun reportUsage(context: Context, location: Location) {
        context.report(
            issue = ISSUE,
            location = location,
            message = "You should only create one OkHttpClient instance"
        )
    }

    companion object {
        private const val OKHTTP_CLIENT = "okhttp3.OkHttpClient"

        private val IMPLEMENTATION = Implementation(
            MoreThanOneOkHttpClientCallGraphDetector::class.java,
            Scope.JAVA_FILE_SCOPE
        )

        val ISSUE: Issue = Issue.create(
            id = "MoreThanOneOkHttpClientDetector",
            briefDescription = "You should only create one OkHttpClient instance",
            explanation = """
                    According to the official docs, 
                    "OkHttp performs best when you create a single OkHttpClient instance and reuse it for all of your HTTP calls.
                    This is because each client holds its own connection pool and thread pools. 
                    Reusing connections and threads reduces latency and saves memory. 
                    Conversely, creating a client for each request wastes resources on idle pools."
                    More details at https://square.github.io/okhttp/4.x/okhttp/okhttp3/-ok-http-client/
                """.trimIndent(),
            category = Category.CORRECTNESS,
            priority = 5,
            severity = Severity.WARNING,
            androidSpecific = true,
            implementation = IMPLEMENTATION
        )
    }

}