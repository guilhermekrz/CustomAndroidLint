package com.brokoli.rules

import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Issue
import org.junit.jupiter.api.Test

@Suppress("UnstableApiUsage")
class MoreThanOneOkHttpClientCallGraphDetectorTest : AndroidSdkLintDetectorTest() {

    override fun getDetector(): Detector = MoreThanOneOkHttpClientCallGraphDetector()

    override fun getIssues(): MutableList<Issue> = mutableListOf(MoreThanOneOkHttpClientCallGraphDetector.ISSUE)

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
                src/com/brokoli/lint/MyClass.java:7: Warning: You should only create one OkHttpClient instance [MoreThanOneOkHttpClientDetector]
                    public void method1() {
                    ^
                src/com/brokoli/lint/MyClass.java:11: Warning: You should only create one OkHttpClient instance [MoreThanOneOkHttpClientDetector]
                    public void method2() {
                    ^
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
                src/com/brokoli/lint/MyClass.java:7: Warning: You should only create one OkHttpClient instance [MoreThanOneOkHttpClientDetector]
                    public void method1() {
                    ^
                src/com/brokoli/lint/MyClass.kt:7: Warning: You should only create one OkHttpClient instance [MoreThanOneOkHttpClientDetector]
                    fun method1() {
                    ^
                0 errors, 2 warnings
            """.trimIndent())
    }

    @Test
    fun `test multiple calls to method which created OkHttpClient`() {
        val kotlinFile = kotlin(
            """
            package com.brokoli.lint
            
            import okhttp3.OkHttpClient

            class MyClass {
                
                fun method1() {
                    OkHttpClient()
                }
                
                fun method2() {
                    method1()
                }
                
                fun method3() {
                    method1()
                }
                
            }
        """
        ).indented()

        val lintResult = lint()
            .files(okHttpClientFile, kotlinFile)
            .run()

        lintResult
            .expectWarningCount(2)
            .expect("""
                src/com/brokoli/lint/MyClass.kt:11: Warning: You should only create one OkHttpClient instance [MoreThanOneOkHttpClientDetector]
                    fun method2() {
                    ^
                src/com/brokoli/lint/MyClass.kt:15: Warning: You should only create one OkHttpClient instance [MoreThanOneOkHttpClientDetector]
                    fun method3() {
                    ^
                0 errors, 2 warnings
            """.trimIndent())
    }

    @Test
    fun `test multiple calls to method which created OkHttpClient in multiple files`() {
        val javaFile = java(
            """
            package com.brokoli.lint;
            
            import okhttp3.OkHttpClient;

            class JavaClass {
            
                public void method1() {
                    new KotlinClass().method2();
                }
                
                public void method2() {
                    new KotlinClass().method2();
                }
                
            }
        """
        ).indented()
        val kotlinFile = kotlin(
            """
            package com.brokoli.lint
            
            import okhttp3.OkHttpClient

            class KotlinClass {
                
                fun method1() {
                    OkHttpClient()
                }
                
                fun method2() {
                    method1()
                }
                
            }
        """
        ).indented()

        val lintResult = lint()
            .files(okHttpClientFile, javaFile, kotlinFile)
            .run()

        lintResult
            .expectWarningCount(1)
            .expect("""
                src/okhttp3/OkHttpClient.java:5: Warning: You should only create one OkHttpClient instance [MoreThanOneOkHttpClientDetector]
                    public OkHttpClient() {
                    ^
                0 errors, 1 warnings
            """.trimIndent())
    }

}