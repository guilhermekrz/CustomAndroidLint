package com.brokoli.rules

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.*

@Suppress("UnstableApiUsage")
class ImmutableListDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>>? {
        return listOf(UCallExpression::class.java)
    }

    override fun createUastHandler(context: JavaContext): UElementHandler? {
        return CollectionHandler(context)
    }

    inner class CollectionHandler(private val context: JavaContext) : UElementHandler() {

        //java.util.List<? extends java.lang.Object>
        // context.evaluator.findClass("java.util.List").allMethods
        override fun visitCallExpression(node: UCallExpression) {
            node.asRecursiveLogString()
        }

    }

    private fun reportUsage(context: JavaContext, node: UCallExpression) {
        context.report(
            issue = ISSUE,
            location = context.getLocation(
                element = node
            ),
            message = "You are calling a Java method which needs a mutable Collection"
        )
    }

    companion object {
        private val IMPLEMENTATION = Implementation(
            ImmutableListDetector::class.java,
            Scope.JAVA_FILE_SCOPE
        )

        val ISSUE: Issue = Issue
            .create(
                id = "ImmutableListDetector",
                briefDescription = "You are calling a Java method which needs a mutable Collection",
                explanation = """
                    You are calling a Java method which needs a mutable Collection
                """.trimIndent(),
                category = Category.CORRECTNESS,
                priority = 9,
                severity = Severity.ERROR,
                androidSpecific = true,
                implementation = IMPLEMENTATION
            )
    }

}