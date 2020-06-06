package com.brokoli.rules

import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Issue
import org.junit.jupiter.api.Test

@Suppress("UnstableApiUsage")
class MoreThanOneOkHttpClientDetectorTest : AndroidSdkLintDetectorTest() {

    override fun getDetector(): Detector = MoreThanOneOkHttpClientDetector()

    override fun getIssues(): MutableList<Issue> = mutableListOf(MoreThanOneOkHttpClientDetector.ISSUE)

    private val okHttpClientFile = java(
        """
            package okhttp3;

            class OkHttpClient {
                
                public OkHttpClient() {
                }
                
            }
        """
    ).indented()

    @Test
    fun `no call to OkHttpClient constructor`() {
        val javaFile = java(
            """
            package com.brokoli.lint;

            class MyClass {
                
                public void method1() {
                    
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
    fun `one call to OkHttpClient constructor`() {
        val javaFile = java(
            """
            package com.brokoli.lint;
            
            import okhttp3.OkHttpClient;

            class MyClass {
                
                public void method1() {
                    new OkHttpClient();
                }
                
            }
        """
        ).indented()

        val lintResult = lint()
            .files(okHttpClientFile, javaFile)
            .run()

        lintResult.expectClean()
    }

    @Test
    fun `two calls to OkHttpClient constructor in the same file`() {
        val javaFile = java(
            """
            package com.brokoli.lint;
            
            import okhttp3.OkHttpClient;

            class MyClass {
                
                public void method1() {
                    new OkHttpClient();
                }
                
                public void method2() {
                    new OkHttpClient();
                }
                
            }
        """
        ).indented()

        val lintResult = lint()
            .files(okHttpClientFile, javaFile)
            .run()

        lintResult
            .expectWarningCount(2)
            .expect("""
                src/com/brokoli/lint/MyClass.java:8: Warning: You should only create one OkHttpClient instance [MoreThanOneOkHttpClientDetector]
                        new OkHttpClient();
                        ~~~~~~~~~~~~~~~~~~
                src/com/brokoli/lint/MyClass.java:12: Warning: You should only create one OkHttpClient instance [MoreThanOneOkHttpClientDetector]
                        new OkHttpClient();
                        ~~~~~~~~~~~~~~~~~~
                0 errors, 2 warnings
            """.trimIndent())
    }

    @Test
    fun `two calls to OkHttpClient constructor in different files`() {
        val javaFile = java(
            """
            package com.brokoli.lint;
            
            import okhttp3.OkHttpClient;

            class MyClass {
                
                public void method1() {
                    new OkHttpClient();
                }
                
            }
        """
        ).indented()
        val kotlinFile = kotlin(
            """
            package com.brokoli.lint
            
            import okhttp3.OkHttpClient

            class MyClass {
                
                fun method1() {
                    OkHttpClient()
                }
                
            }
        """
        ).indented()

        val lintResult = lint()
            .files(okHttpClientFile, javaFile, kotlinFile)
            .run()

        lintResult
            .expectWarningCount(2)
            .expect("""
                src/com/brokoli/lint/MyClass.java:8: Warning: You should only create one OkHttpClient instance [MoreThanOneOkHttpClientDetector]
                        new OkHttpClient();
                        ~~~~~~~~~~~~~~~~~~
                src/com/brokoli/lint/MyClass.kt:8: Warning: You should only create one OkHttpClient instance [MoreThanOneOkHttpClientDetector]
                        OkHttpClient()
                        ~~~~~~~~~~~~~~
                0 errors, 2 warnings
            """.trimIndent())
    }

}