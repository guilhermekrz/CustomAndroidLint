package com.brokoli.rules

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.intellij.lang.jvm.types.JvmReferenceType
import com.intellij.psi.PsiType
import com.intellij.psi.impl.source.PsiClassReferenceType
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UElement
import org.jetbrains.uast.kotlin.KotlinUCatchClause
import org.jetbrains.uast.kotlin.KotlinUTryExpression
import org.jetbrains.uast.visitor.AbstractUastVisitor

@Suppress("UnstableApiUsage")
class KotlinShouldHandleJavaExceptionsDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>>? {
        return listOf(UCallExpression::class.java)
    }

    override fun createUastHandler(context: JavaContext): UElementHandler? {
        return KotlinThrowsHandler(context)
    }

    inner class KotlinThrowsHandler(private val context: JavaContext) : UElementHandler() {

        override fun visitCallExpression(node: UCallExpression) {
            node.accept(AnotherVisitor(context))
        }

    }

    private inner class AnotherVisitor(private val context: JavaContext) : AbstractUastVisitor() {

        override fun visitCallExpression(node: UCallExpression): Boolean {
            val throwTypes = node.resolve()?.throwsTypes.orEmpty()
            if(throwTypes.isNotEmpty()) {
                if(!hasTryCatch(node.uastParent, throwTypes.toList())) {
                    reportUsage(context, node)
                }
            }
            return super.visitCallExpression(node)
        }

        private fun hasTryCatch(node: UElement?, throwTypes: List<JvmReferenceType>): Boolean {
            if(node == null) {
                return false
            }
            if(node is KotlinUCatchClause) {
                return false
            }
            if(node is KotlinUTryExpression) {
                if(catchExceptions(throwTypes, node.catchClauses)) {
                    return true
                }
            }
            return hasTryCatch(node.uastParent, throwTypes)
        }

        private fun catchExceptions(throwTypes: List<JvmReferenceType>, catchClauses: List<KotlinUCatchClause>): Boolean {
            val catchTypes = catchClauses.flatMap { catchClause -> catchClause.types }
            return throwTypes.all { throwType ->
                catchTypes.any { catchType ->
                    catchException((throwType as PsiClassReferenceType).deepComponentType, catchType)
                }
            }
        }

        private fun catchException(throwType: PsiType, compareType: PsiType): Boolean {
            if(throwType.canonicalText == compareType.canonicalText) {
                return true
            }
            for(throwSuperType in throwType.superTypes) {
                if(catchException(throwSuperType, compareType)) {
                    return true
                }
            }
            return false
        }

    }

    private fun reportUsage(context: JavaContext, node: UCallExpression) {
        context.report(
            issue = ISSUE,
            location = context.getLocation(
                element = node
            ),
            message = "Kotlin code is calling Java method which throws checked exceptions. Be aware to handle it accordingly!"
        )
    }

    companion object {
        private val IMPLEMENTATION = Implementation(
            KotlinShouldHandleJavaExceptionsDetector::class.java,
            Scope.JAVA_FILE_SCOPE
        )

        val ISSUE: Issue = Issue
            .create(
                id = "KotlinShouldHandleJavaExceptionsDetector",
                briefDescription = "Kotlin code is calling Java method which throws checked exceptions",
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