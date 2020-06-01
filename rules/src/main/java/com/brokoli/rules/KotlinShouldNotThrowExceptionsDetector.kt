package com.brokoli.rules

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.intellij.psi.PsiType
import org.jetbrains.uast.*
import org.jetbrains.uast.kotlin.KotlinUThrowExpression
import org.jetbrains.uast.visitor.AbstractUastVisitor
import java.lang.IllegalStateException
import java.lang.RuntimeException

// Detects that Kotlin methods:
// 1 - Do not have @Throws annotation
// 2 - Do not have throw expressions
@Suppress("UnstableApiUsage")
class KotlinShouldNotThrowExceptionsDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>>? {
        return listOf(UMethod::class.java, UCallExpression::class.java)
    }

    override fun createUastHandler(context: JavaContext): UElementHandler? {
        return KotlinThrowsHandler(context)
    }

    private inner class KotlinThrowsHandler(private val context: JavaContext) : UElementHandler() {

        override fun visitMethod(node: UMethod) {
            for(annotation in node.annotations) {
                if(annotation.qualifiedName == Throws::class.qualifiedName) {
                    reportUsage(context, location = context.getLocation(node = node))
                }
            }
        }

        override fun visitCallExpression(node: UCallExpression) {
            node.accept(ThrowCallVisitor(context))
        }

    }

    private inner class ThrowCallVisitor(private val context: JavaContext) : AbstractUastVisitor() {

        override fun visitCallExpression(node: UCallExpression): Boolean {
            if(node.uastParent is KotlinUThrowExpression) {
                val type = node.returnType ?: throw IllegalStateException("type of KotlinUThrowExpression cannot be null")
                if(!isUncheckedException(type)) {
                    reportUsage(context, context.getLocation(element = node))
                }
            }
            return false
        }

        private fun isUncheckedException(type: PsiType): Boolean {
            if(type.canonicalText == RuntimeException::class.java.canonicalName) {
                return true
            }
            for(superType in type.superTypes) {
                if(isUncheckedException(superType)) {
                    return true
                }
            }
            return false
        }

    }

    private fun reportUsage(context: JavaContext, location: Location) {
        context.report(
            issue = ISSUE,
            location = location,
            message = "Kotlin code should not throw Exceptions"
        )
    }

    companion object {
        private val IMPLEMENTATION = Implementation(
            KotlinShouldNotThrowExceptionsDetector::class.java,
            Scope.JAVA_FILE_SCOPE
        )

        val ISSUE: Issue = Issue
            .create(
                id = "KotlinShouldNotThrowExceptionsDetector",
                briefDescription = "Kotlin code should not throw Exceptions",
                explanation = """
                    Kotlin does not support checked Exceptions, so we should not rely on Exceptions to propagate errors in our application.
                    More details in: https://kotlinlang.org/docs/reference/exceptions.html
                """.trimIndent(),
                category = Category.CORRECTNESS,
                priority = 9,
                severity = Severity.ERROR,
                androidSpecific = true,
                implementation = IMPLEMENTATION
            )
    }
}
