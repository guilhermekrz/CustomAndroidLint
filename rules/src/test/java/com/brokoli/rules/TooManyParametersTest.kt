package com.brokoli.rules

import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Issue
import org.junit.jupiter.api.Test

@Suppress("UnstableApiUsage")
class TooManyParametersDetectorTest : AndroidSdkLintDetectorTest() {

    override fun getDetector(): Detector = TooManyParametersDetector()

    override fun getIssues(): MutableList<Issue> = mutableListOf(TooManyParametersDetector.ISSUE)

    @Test
    fun `java method with 0 parameters`() {
        val javaFile = java(
            """
            package com.brokoli.lint;

            class MyClass {
                
                public void methodWith0Parameters() {
                    
                }
                
            }
        """
        ).indented()

        val lintResult = lint()
            .files(javaFile)
            .run()

        lintResult.expectClean()
    }

    @Test
    fun `java method with 1 parameter`() {
        val javaFile = java(
            """
            package com.brokoli.lint;

            class MyClass {
                
                public void methodWith1Parameter(boolean first) {
                    
                }
                
            }
        """
        ).indented()

        val lintResult = lint()
            .files(javaFile)
            .run()

        lintResult.expectClean()
    }

    @Test
    fun `java method with 5 parameters`() {
        val javaFile = java(
            """
            package com.brokoli.lint;

            class MyClass {
                
                public void methodWith5Parameters(boolean first, String second, int third, long fourth, char fifth) {
                    
                }
                
            }
        """
        ).indented()

        val lintResult = lint()
            .files(javaFile)
            .run()

        lintResult.expectClean()
    }

    @Test
    fun `java method with 6 parameters`() {
        val javaFile = java(
            """
            package com.brokoli.lint;

            class MyClass {
                
                public void methodWith6Parameters(boolean first, String second, int third, long fourth, char fifth, boolean sixth) {
                    
                }
                
            }
        """
        ).indented()

        val lintResult = lint()
            .files(javaFile)
            .run()

        lintResult
            .expectWarningCount(1)
            .expect(
                """
                    src/com/brokoli/lint/MyClass.java:5: Warning: Method should not declare more than 5 parameters [TooManyParametersDetector]
                        public void methodWith6Parameters(boolean first, String second, int third, long fourth, char fifth, boolean sixth) {
                        ^
                    0 errors, 1 warnings
                """.trimIndent()
            )
    }

    @Test
    fun `kotlin method with 0 parameters`() {
        val kotlinFile = kotlin(
            """
            package com.brokoli.lint;

            class MyClass {
                
                fun methodWith0Parameters() {
                    
                }
                
            }
        """
        ).indented()

        val lintResult = lint()
            .files(kotlinFile)
            .run()

        lintResult.expectClean()
    }

    @Test
    fun `kotlin method with 1 parameter`() {
        val kotlinFile = kotlin(
            """
            package com.brokoli.lint;

            class MyClass {
                
                fun methodWith1Parameter(first: Boolean) {
                    
                }
                
            }
        """
        ).indented()

        val lintResult = lint()
            .files(kotlinFile)
            .run()

        lintResult.expectClean()
    }

    @Test
    fun `kotlin method with 5 parameters`() {
        val kotlinFile = kotlin(
            """
            package com.brokoli.lint;

            class MyClass {
                
                fun methodWith5Parameters(first: Boolean, second: Boolean, third: Boolean, fourth: Boolean, fifth: Boolean) {
                    
                }
                
            }
        """
        ).indented()

        val lintResult = lint()
            .files(kotlinFile)
            .run()

        lintResult.expectClean()
    }

    @Test
    fun `kotlin method with 6 parameters`() {
        val kotlinFile = kotlin(
            """
            package com.brokoli.lint;

            class MyClass {
                
                fun methodWith6Parameters(first: Boolean, second: String, third: Int, fourth: Long, fifth: Char, sixth: Boolean) {
                    
                }
                
            }
        """
        ).indented()

        val lintResult = lint()
            .files(kotlinFile)
            .run()

        lintResult
            .expectWarningCount(1)
            .expect(
                """
                src/com/brokoli/lint/MyClass.kt:5: Warning: Method should not declare more than 5 parameters [TooManyParametersDetector]
                    fun methodWith6Parameters(first: Boolean, second: String, third: Int, fourth: Long, fifth: Char, sixth: Boolean) {
                    ^
                0 errors, 1 warnings
                """.trimIndent()
            )
    }

    @Test
    fun `kotlin method with 0 and 6 parameters`() {
        val kotlinFile = kotlin(
            """
            package com.brokoli.lint;

            class MyClass {
            
                fun methodWith0Parameters() {
                    
                }
                
                fun methodWith6Parameters(first: Boolean, second: String, third: Int, fourth: Long, fifth: Char, sixth: Boolean) {
                    
                }
                
            }
        """
        ).indented()

        val lintResult = lint()
            .files(kotlinFile)
            .run()

        lintResult
            .expectWarningCount(1)
            .expect(
                """
                src/com/brokoli/lint/MyClass.kt:9: Warning: Method should not declare more than 5 parameters [TooManyParametersDetector]
                    fun methodWith6Parameters(first: Boolean, second: String, third: Int, fourth: Long, fifth: Char, sixth: Boolean) {
                    ^
                0 errors, 1 warnings
                """.trimIndent()
            )
    }

    @Test
    fun `kotlin method with 6 and 8 parameters`() {
        val kotlinFile = kotlin(
            """
            package com.brokoli.lint;

            class MyClass {
                
                fun methodWith6Parameters(first: Boolean, second: String, third: Int, fourth: Long, fifth: Char, sixth: Boolean) {
                    
                }
                
                fun methodWith8Parameters(first: Boolean, second: String, third: Int, fourth: Long, fifth: Char, sixth: Boolean, seventh: Boolean, eight: Boolean) {
                    
                }
                
            }
        """
        ).indented()

        val lintResult = lint()
            .files(kotlinFile)
            .run()

        lintResult
            .expectWarningCount(2)
            .expect(
                """
                    src/com/brokoli/lint/MyClass.kt:5: Warning: Method should not declare more than 5 parameters [TooManyParametersDetector]
                        fun methodWith6Parameters(first: Boolean, second: String, third: Int, fourth: Long, fifth: Char, sixth: Boolean) {
                        ^
                    src/com/brokoli/lint/MyClass.kt:9: Warning: Method should not declare more than 5 parameters [TooManyParametersDetector]
                        fun methodWith8Parameters(first: Boolean, second: String, third: Int, fourth: Long, fifth: Char, sixth: Boolean, seventh: Boolean, eight: Boolean) {
                        ^
                    0 errors, 2 warnings
                """.trimIndent()
            )
    }

}