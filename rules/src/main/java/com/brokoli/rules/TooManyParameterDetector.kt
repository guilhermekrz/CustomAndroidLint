package com.brokoli.rules

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UMethod

@Suppress("UnstableApiUsage")
class TooManyParameterDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>>? {
        return listOf(UMethod::class.java)
    }

    override fun createUastHandler(context: JavaContext): UElementHandler? {
        return MethodHandler(context)
    }

    inner class MethodHandler(private val context: JavaContext) : UElementHandler() {

        override fun visitMethod(node: UMethod) {
            if(node.parameters.size > MAX_NUMBER_OF_METHOD_PARAMETERS) {
                reportUsage(context, context.getLocation(node))
            }
        }

    }

    private fun reportUsage(context: JavaContext, location: Location) {
        context.report(
            issue = ISSUE,
            location = location,
            message = "Method should not declare more than 5 parameters"
        )
    }

    companion object {

        private const val MAX_NUMBER_OF_METHOD_PARAMETERS = 5

        private val IMPLEMENTATION = Implementation(
            TooManyParameterDetector::class.java,
            Scope.JAVA_FILE_SCOPE
        )

        val ISSUE: Issue = Issue.create(
            id = "TooManyParameterDetector",
            briefDescription = "Method should not declare more than 5 parameters",
            explanation = """
                    You should limit the number of parameters you method receive, 
                    in order to make your method more legible and easier to use correctly.
                """.trimIndent(),
            category = Category.CORRECTNESS,
            priority = 5,
            severity = Severity.WARNING,
            androidSpecific = true,
            implementation = IMPLEMENTATION
        )
    }

}