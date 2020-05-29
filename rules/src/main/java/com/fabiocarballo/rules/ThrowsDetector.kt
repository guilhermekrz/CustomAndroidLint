package com.fabiocarballo.rules

import com.android.tools.lint.detector.api.*
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UCallExpression

@Suppress("UnstableApiUsage")
class ThrowsDetector : Detector(), SourceCodeScanner {

    override fun getApplicableMethodNames(): List<String> =
        listOf("tag", "format", "v", "d", "i", "w", "e", "wtf")

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        super.visitMethodCall(context, node, method)
        val evaluator = context.evaluator
        if (evaluator.isMemberInClass(method, "android.util.Log")) {
            reportUsage(context, node)
        }
    }

    private fun reportUsage(context: JavaContext, node: UCallExpression) {
        context.report(
            issue = ISSUE,
            scope = node,
            location = context.getCallLocation(
                call = node,
                includeReceiver = true,
                includeArguments = true
            ),
            message = "throws"
        )
    }

    companion object {
        private val IMPLEMENTATION = Implementation(
            ThrowsDetector::class.java,
            Scope.JAVA_FILE_SCOPE
        )

        val ISSUE: Issue = Issue
            .create(
                id = "ThrowsDetector",
                briefDescription = "Should handle throws",
                explanation = """
Amazing explanation
            """.trimIndent(),
                category = Category.CORRECTNESS,
                priority = 9,
                severity = Severity.ERROR,
                androidSpecific = true,
                implementation = IMPLEMENTATION
            )
    }
}
