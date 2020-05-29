package com.fabiocarballo.rules

import com.android.tools.lint.checks.infrastructure.LintDetectorTest
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Issue
import com.fabiocarballo.rules.Stubs.ANDROID_LOG_IMPL_JAVA
import com.fabiocarballo.rules.Stubs.CUSTOM_LOG_IMPL_JAVA
import org.junit.jupiter.api.Test

@Suppress("UnstableApiUsage")
class ThrowsDetectorTest : LintDetectorTest() {

    @Test
    fun detectThrows() {
        val kotlinFile = kotlin(
            """
            package com.fabiocarballo.lint

            class ClassThatDoesNotHandleThrows {
                
                fun method() {
                    ClassThatThrows().methodThrows()
                }
                
            }
        """
        ).indented()

        val javaFile = java(
            """
            package com.fabiocarballo.lint;

            class ClassThatThrows {
            
                public void methodThrows() throws IllegalAccessException {
                    throw new IllegalAccessException();
                }
            
            }
        """
        ).indented()

        val lintResult = lint()
            .files(javaFile, kotlinFile)
            .run()

        lintResult
            .expectErrorCount(1)
            .expect(
                """
             src/com/fabiocarballo/lint/Dog.kt:8: Error: android.util.Log usage is forbidden. [AndroidLogDetector]
                     Log.d(TAG, "woof! woof!")
                     ~~~~~~~~~~~~~~~~~~~~~~~~~
             1 errors, 0 warnings
         """.trimIndent()
            )
    }

    override fun getDetector(): Detector = AndroidLogDetector()

    override fun getIssues(): MutableList<Issue> = mutableListOf(AndroidLogDetector.ISSUE)
}