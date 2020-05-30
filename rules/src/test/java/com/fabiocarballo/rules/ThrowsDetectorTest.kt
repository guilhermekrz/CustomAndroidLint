package com.fabiocarballo.rules

import com.android.tools.lint.checks.infrastructure.LintDetectorTest
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Issue
import com.fabiocarballo.rules.Stubs.ANDROID_LOG_IMPL_JAVA
import com.fabiocarballo.rules.Stubs.CUSTOM_LOG_IMPL_JAVA
import org.junit.jupiter.api.Test

@Suppress("UnstableApiUsage")
class ThrowsDetectorTest : LintDetectorTest() {

    override fun getDetector(): Detector = ThrowsDetector()

    override fun getIssues(): MutableList<Issue> = mutableListOf(ThrowsDetector.ISSUE)

    @Test
    fun detectDoNotThrow() {
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
            
                public void methodThrows() {
                    throw new IllegalAccessException();
                }
            
            }
        """
        ).indented()

        val lintResult = lint()
            .files(javaFile, kotlinFile)
            .run()

        lintResult.expectClean()
    }

    @Test
    fun detectTryCatch() {
        val kotlinFile = kotlin(
            """
            package com.fabiocarballo.lint

            class ClassThatDoesNotHandleThrows {
                
                fun method() {
                    try {
                        ClassThatThrows().methodThrows()
                    } catch (e: IllegalAccessException) {
                        
                    }
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

        lintResult.expectClean()
    }

    @Test
    fun detectThrowsIllegalAccessException() {
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
             src/com/fabiocarballo/lint/ClassThatDoesNotHandleThrows.kt:6: Error: throws [ThrowsDetector]
                     ClassThatThrows().methodThrows()
                     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
             1 errors, 0 warnings
         """.trimIndent()
            )
    }

    @Test
    fun detectThrowsNullPointerException() {
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
            
                public void methodThrows() throws NullPointerException {
                    throw new NullPointerException();
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
             src/com/fabiocarballo/lint/ClassThatDoesNotHandleThrows.kt:6: Error: throws [ThrowsDetector]
                     ClassThatThrows().methodThrows()
                     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
             1 errors, 0 warnings
         """.trimIndent()
            )
    }
}