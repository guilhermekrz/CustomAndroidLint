package com.brokoli.rules

import com.android.tools.lint.detector.api.*
import org.junit.jupiter.api.Test

@Suppress("UnstableApiUsage")
class ImmutableListDetectorTest : AndroidSdkLintDetectorTest() {

    override fun getDetector(): Detector = ImmutableListDetector()

    override fun getIssues(): MutableList<Issue> = mutableListOf(ImmutableListDetector.ISSUE)

    @Test
    fun `pass mutable list to Java method which does not mutate list`() {
        val javaFile = java(
            """
            package com.brokoli.lint;
            
            import java.util.List;

            class JavaClass {
                
                public void methodChangesList(List<Object> objects) {
                
                }
                
            }
        """
        ).indented()
        val kotlinFile = kotlin(
            """
            package com.brokoli.lint

            class KotlinClass {
                
                fun kotlinMethod() {
                    JavaClass().methodChangesList(mutableListOf())
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
    fun `pass mutable list to Java method which mutates list`() {
        val javaFile = java(
            """
            package com.brokoli.lint;
            
            import java.util.List;

            class JavaClass {
                
                public void methodChangesList(List<Object> objects) {
                    objects.add(new Object());
                }
                
            }
        """
        ).indented()
        val kotlinFile = kotlin(
            """
            package com.brokoli.lint

            class KotlinClass {
                
                fun kotlinMethod() {
                    JavaClass().methodChangesList(mutableListOf())
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
    fun `pass immutable list to Java method which mutates list`() {
        val javaFile = java(
            """
            package com.brokoli.lint;
            
            import java.util.List;

            class JavaClass {
                
                public void methodChangesList(List<Object> objects) {
                    objects.add(new Object());
                }
                
            }
        """
        ).indented()
        val kotlinFile = kotlin(
            """
            package com.brokoli.lint

            class KotlinClass {
                
                fun kotlinMethod() {
                    val list = mutableListOf<Object>()
                    JavaClass().methodChangesList(list)
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
             src/com/brokoli/lint/MyKotlinClass.kt:6: Error: Kotlin code is calling Java method which throws checked exceptions. Be aware to handle it accordingly! [KotlinShouldHandleJavaExceptionsDetector]
                     MyJavaClass().javaMethod()
                     ~~~~~~~~~~~~~~~~~~~~~~~~~~
             1 errors, 0 warnings
         """.trimIndent()
            )
    }

}