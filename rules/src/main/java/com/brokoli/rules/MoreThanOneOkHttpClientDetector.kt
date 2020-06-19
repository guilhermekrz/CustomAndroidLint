package com.brokoli.rules

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UastCallKind

@Suppress("UnstableApiUsage")
class MoreThanOneOkHttpClientDetector : Detector(), SourceCodeScanner {

    private val okHttpClientConstructors = mutableSetOf<CallContextLocation>()

    data class CallContextLocation(val context: JavaContext, val location: Location)

    override fun getApplicableUastTypes(): List<Class<out UElement>>? {
        return listOf(UCallExpression::class.java)
    }

    override fun createUastHandler(context: JavaContext): UElementHandler? {
        return OkHttpClientHandler(context)
    }

    inner class OkHttpClientHandler(private val context: JavaContext) : UElementHandler() {

        override fun visitCallExpression(node: UCallExpression) {
            if(node.getExpressionType()?.canonicalText == OKHTTP_CLIENT && node.kind == UastCallKind.CONSTRUCTOR_CALL) {
                okHttpClientConstructors.add(CallContextLocation(context, context.getLocation(node)))
            }
        }

    }

    override fun afterCheckRootProject(context: Context) {
        if(okHttpClientConstructors.size > 1) {
            for(callContextLocation in okHttpClientConstructors) {
                reportUsage(callContextLocation.context, callContextLocation.location)
            }
        }
    }

    private fun reportUsage(context: JavaContext, location: Location) {
        context.report(
            issue = ISSUE,
            location = location,
            message = "You should only create one OkHttpClient instance"
        )
    }

    companion object {
        private const val OKHTTP_CLIENT = "okhttp3.OkHttpClient"

        private val IMPLEMENTATION = Implementation(
            MoreThanOneOkHttpClientDetector::class.java,
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