package com.brokoli.rules

import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Issue
import org.junit.jupiter.api.Test

@Suppress("UnstableApiUsage")
class KotlinShouldHandleJavaExceptionsDetectorTest : AndroidSdkLintDetectorTest() {

    override fun getDetector(): Detector = KotlinShouldHandleJavaExceptionsDetector()

    override fun getIssues(): MutableList<Issue> = mutableListOf(KotlinShouldHandleJavaExceptionsDetector.ISSUE)

    @Test
    fun detectKotlinCallsJavaMethodWhichThrowsException() {
        val javaFile = java(
            """
            package com.brokoli.lint;
            
            import java.io.IOException;

            class MyJavaClass {
                
                public void javaMethod() throws IOException {
                    throw new IOException();
                }
                
            }
        """
        ).indented()
        val kotlinFile = kotlin(
            """
            package com.brokoli.lint

            class MyKotlinClass {
                
                fun kotlinMethod() {
                    MyJavaClass().javaMethod()
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

    @Test
    fun detectKotlinCallsJavaMethodWhichThrowsExceptionWithTryCatch() {
        val javaFile = java(
            """
            package com.brokoli.lint;
            
            import java.io.IOException;

            class MyJavaClass {
                
                public void javaMethod() throws IOException {
                    throw new IOException();
                }
                
            }
        """
        ).indented()
        val kotlinFile = kotlin(
            """
            package com.brokoli.lint
            
            import java.io.IOException

            class MyKotlinClass {
                
                fun kotlinMethod() {
                    try {
                        MyJavaClass().javaMethod()
                    } catch (exception: IOException) {
                        
                    }
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
    fun detectKotlinCallsJavaMethodWhichThrowsExceptionWithTryCatchWithException() {
        val javaFile = java(
            """
            package com.brokoli.lint;
            
            import java.io.IOException;

            class MyJavaClass {
                
                public void javaMethod() throws IOException {
                    throw new IOException();
                }
                
            }
        """
        ).indented()
        val kotlinFile = kotlin(
            """
            package com.brokoli.lint
            
            import java.lang.Exception

            class MyKotlinClass {
                
                fun kotlinMethod() {
                    try {
                        MyJavaClass().javaMethod()
                    } catch (exception: Exception) {
                        
                    }
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
    fun detectKotlinCallsJavaMethodWhichThrowsExceptionWithWrongTryCatch() {
        val javaFile = java(
            """
            package com.brokoli.lint;
            
            import java.io.IOException;

            class MyJavaClass {
                
                public void javaMethod() throws IOException {
                    throw new IOException();
                }
                
            }
        """
        ).indented()
        val kotlinFile = kotlin(
            """
            package com.brokoli.lint
            
            import java.io.IOException

            class MyKotlinClass {
                
                fun kotlinMethod() {
                    try {
                    } catch (exception: IOException) {
                        
                    }
                    MyJavaClass().javaMethod()
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
             src/com/brokoli/lint/MyKotlinClass.kt:12: Error: Kotlin code is calling Java method which throws checked exceptions. Be aware to handle it accordingly! [KotlinShouldHandleJavaExceptionsDetector]
                     MyJavaClass().javaMethod()
                     ~~~~~~~~~~~~~~~~~~~~~~~~~~
             1 errors, 0 warnings
         """.trimIndent()
            )
    }

    @Test
    fun detectKotlinCallsJavaMethodWhichThrowsExceptionWithWrongCatch() {
        val javaFile = java(
            """
            package com.brokoli.lint;
            
            import java.io.IOException;

            class MyJavaClass {
                
                public void javaMethod() throws IOException {
                    throw new IOException();
                }
                
            }
        """
        ).indented()
        val kotlinFile = kotlin(
            """
            package com.brokoli.lint
            
            import java.io.IOException

            class MyKotlinClass {
                
                fun kotlinMethod() {
                    try {
                    } catch (exception: IOException) {
                        MyJavaClass().javaMethod()
                    }
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
             src/com/brokoli/lint/MyKotlinClass.kt:10: Error: Kotlin code is calling Java method which throws checked exceptions. Be aware to handle it accordingly! [KotlinShouldHandleJavaExceptionsDetector]
                         MyJavaClass().javaMethod()
                         ~~~~~~~~~~~~~~~~~~~~~~~~~~
             1 errors, 0 warnings
         """.trimIndent()
            )
    }

    @Test
    fun detectKotlinCallsJavaMethodWhichThrowsExceptionWithTwoTryCatch() {
        val javaFile = java(
            """
            package com.brokoli.lint;
            
            import java.io.IOException;

            class MyJavaClass {
                
                public void javaMethod() throws IOException {
                    throw new IOException();
                }
                
            }
        """
        ).indented()
        val kotlinFile = kotlin(
            """
            package com.brokoli.lint
            
            import java.io.IOException

            class MyKotlinClass {
                
                fun kotlinMethod() {
                    try {
                        MyJavaClass().javaMethod()
                    } catch (exception: IOException) {
                        
                    }
                    MyJavaClass().javaMethod()
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
             src/com/brokoli/lint/MyKotlinClass.kt:13: Error: Kotlin code is calling Java method which throws checked exceptions. Be aware to handle it accordingly! [KotlinShouldHandleJavaExceptionsDetector]
                     MyJavaClass().javaMethod()
                     ~~~~~~~~~~~~~~~~~~~~~~~~~~
             1 errors, 0 warnings
         """.trimIndent()
            )
    }


    @Test
    fun detectKotlinCallsJavaMethodWhichThrowsExceptionWithTwoWrongTryCatch() {
        val javaFile = java(
            """
            package com.brokoli.lint;
            
            import java.io.IOException;

            class MyJavaClass {
                
                public void javaMethod() throws IOException {
                    throw new IOException();
                }
                
            }
        """
        ).indented()
        val kotlinFile = kotlin(
            """
            package com.brokoli.lint
            
            import java.io.IOException

            class MyKotlinClass {
                
                fun kotlinMethod() {
                    try {
                    } catch (exception: IOException) {
                        
                    }
                    MyJavaClass().javaMethod()
                    MyJavaClass().javaMethod()
                }
                
            }
        """
        ).indented()

        val lintResult = lint()
            .files(javaFile, kotlinFile)
            .run()

        lintResult
            .expectErrorCount(2)
            .expect(
                """
             src/com/brokoli/lint/MyKotlinClass.kt:12: Error: Kotlin code is calling Java method which throws checked exceptions. Be aware to handle it accordingly! [KotlinShouldHandleJavaExceptionsDetector]
                     MyJavaClass().javaMethod()
                     ~~~~~~~~~~~~~~~~~~~~~~~~~~
             src/com/brokoli/lint/MyKotlinClass.kt:13: Error: Kotlin code is calling Java method which throws checked exceptions. Be aware to handle it accordingly! [KotlinShouldHandleJavaExceptionsDetector]
                     MyJavaClass().javaMethod()
                     ~~~~~~~~~~~~~~~~~~~~~~~~~~
             2 errors, 0 warnings
         """.trimIndent()
            )
    }

    @Test
    fun detectKotlinCallsJavaMethodWhichThrowsExceptionCachingWrongException() {
        val javaFile = java(
            """
            package com.brokoli.lint;
            
            import java.io.IOException;

            class MyJavaClass {
                
                public void javaMethod() throws IOException {
                    throw new IOException();
                }
                
            }
        """
        ).indented()
        val kotlinFile = kotlin(
            """
            package com.brokoli.lint
            
            import java.lang.IllegalStateException

            class MyKotlinClass {
                
                fun kotlinMethod() {
                    try {
                        MyJavaClass().javaMethod()
                    } catch (exception: IllegalStateException) {
                        
                    }
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
             src/com/brokoli/lint/MyKotlinClass.kt:9: Error: Kotlin code is calling Java method which throws checked exceptions. Be aware to handle it accordingly! [KotlinShouldHandleJavaExceptionsDetector]
                         MyJavaClass().javaMethod()
                         ~~~~~~~~~~~~~~~~~~~~~~~~~~
             1 errors, 0 warnings
         """.trimIndent()
            )
    }

    @Test
    fun detectKotlinCallsJavaMethodWhichThrowsExceptionCachingInNestedCatch() {
        val javaFile = java(
            """
            package com.brokoli.lint;
            
            import java.io.IOException;

            class MyJavaClass {
                
                public void javaMethod() throws IOException {
                    throw new IOException();
                }
                
            }
        """
        ).indented()
        val kotlinFile = kotlin(
            """
            package com.brokoli.lint
            
            import java.lang.IllegalStateException
            import java.io.IOException

            class MyKotlinClass {
                
                fun kotlinMethod() {
                    try {
                        try {
                            MyJavaClass().javaMethod()
                        } catch (exception: IOException) {
                            
                        }
                    } catch (exception: IllegalStateException) {
                            
                    }
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
    fun detectKotlinCallsJavaMethodWhichThrowsExceptionCachingInNotNestedCatch() {
        val javaFile = java(
            """
            package com.brokoli.lint;
            
            import java.io.IOException;

            class MyJavaClass {
                
                public void javaMethod() throws IOException {
                    throw new IOException();
                }
                
            }
        """
        ).indented()
        val kotlinFile = kotlin(
            """
            package com.brokoli.lint
            
            import java.lang.IllegalStateException
            import java.io.IOException

            class MyKotlinClass {
                
                fun kotlinMethod() {
                    try {
                        try {
                            MyJavaClass().javaMethod()
                        } catch (exception: IllegalStateException) {
                            
                        }
                    } catch (exception: IOException) {
                            
                    }
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
    fun detectKotlinCallsJavaMethodWhichThrowsExceptionCachingInFirstCatch() {
        val javaFile = java(
            """
            package com.brokoli.lint;
            
            import java.io.IOException;

            class MyJavaClass {
                
                public void javaMethod() throws IOException {
                    throw new IOException();
                }
                
            }
        """
        ).indented()
        val kotlinFile = kotlin(
            """
            package com.brokoli.lint
            
            import java.lang.IllegalStateException
            import java.io.IOException

            class MyKotlinClass {
                
                fun kotlinMethod() {
                    try {
                        MyJavaClass().javaMethod()
                    } catch (exception: IOException) {
                        
                    } catch (exception: IllegalStateException) {
                            
                    }
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
    fun detectKotlinCallsJavaMethodWhichThrowsExceptionCachingInSecondCatch() {
        val javaFile = java(
            """
            package com.brokoli.lint;
            
            import java.io.IOException;

            class MyJavaClass {
                
                public void javaMethod() throws IOException {
                    throw new IOException();
                }
                
            }
        """
        ).indented()
        val kotlinFile = kotlin(
            """
            package com.brokoli.lint
            
            import java.lang.IllegalStateException
            import java.io.IOException

            class MyKotlinClass {
                
                fun kotlinMethod() {
                    try {
                        MyJavaClass().javaMethod()
                    } catch (exception: IllegalStateException) {
                        
                    } catch (exception: IOException) {
                            
                    }
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
    fun detectKotlinCallsJavaMethodWhichThrowsMultipleExceptionsAndCatchesSeparately() {
        val javaFile = java(
            """
            package com.brokoli.lint;
            
            import java.lang.IllegalStateException;
            import java.io.IOException;

            class MyJavaClass {
                
                public void javaMethod() throws IOException, IllegalStateException {
                    throw new IOException();
                }
                
            }
        """
        ).indented()
        val kotlinFile = kotlin(
            """
            package com.brokoli.lint
            
            import java.lang.IllegalStateException
            import java.io.IOException

            class MyKotlinClass {
                
                fun kotlinMethod() {
                    try {
                        MyJavaClass().javaMethod()
                    } catch (exception: IllegalStateException) {
                        
                    } catch (exception: IOException) {
                            
                    }
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
    fun detectKotlinCallsJavaMethodWhichThrowsMultipleExceptionsAndCatchesTogether() {
        val javaFile = java(
            """
            package com.brokoli.lint;
            
            import java.lang.IllegalStateException;
            import java.io.IOException;

            class MyJavaClass {
                
                public void javaMethod() throws IOException, IllegalStateException {
                    throw new IOException();
                }
                
            }
        """
        ).indented()
        val kotlinFile = kotlin(
            """
            package com.brokoli.lint
            
            import java.lang.IllegalStateException
            import java.io.IOException

            class MyKotlinClass {
                
                fun kotlinMethod() {
                    try {
                        MyJavaClass().javaMethod()
                    } catch (exception: Exception) {
                        
                    }
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
    fun detectKotlinCallsJavaMethodWhichThrowsMultipleExceptionsAndOnlyCatchesOne() {
        val javaFile = java(
            """
            package com.brokoli.lint;
            
            import java.lang.IllegalStateException;
            import java.io.IOException;

            class MyJavaClass {
                
                public void javaMethod() throws IOException, IllegalStateException {
                    throw new IOException();
                }
                
            }
        """
        ).indented()
        val kotlinFile = kotlin(
            """
            package com.brokoli.lint
            
            import java.lang.IllegalStateException
            import java.io.IOException

            class MyKotlinClass {
                
                fun kotlinMethod() {
                    try {
                        MyJavaClass().javaMethod()
                    } catch (exception: IllegalStateException) {
                        
                    }
                }
                
            }
        """
        ).indented()

        val lintResult = lint()
            .files(javaFile, kotlinFile)
            .run()

        lintResult.expectErrorCount(1)
            .expect("""
                src/com/brokoli/lint/MyKotlinClass.kt:10: Error: Kotlin code is calling Java method which throws checked exceptions. Be aware to handle it accordingly! [KotlinShouldHandleJavaExceptionsDetector]
                            MyJavaClass().javaMethod()
                            ~~~~~~~~~~~~~~~~~~~~~~~~~~
                1 errors, 0 warnings
            """.trimIndent())
    }

    @Test
    fun detectKotlinCallsJavaMethodWhichThrowsMultipleExceptionsAndODoNotCatch() {
        val javaFile = java(
            """
            package com.brokoli.lint;
            
            import java.lang.IllegalStateException;
            import java.io.IOException;

            class MyJavaClass {
                
                public void javaMethod() throws IOException, IllegalStateException {
                    throw new IOException();
                }
                
            }
        """
        ).indented()
        val kotlinFile = kotlin(
            """
            package com.brokoli.lint
            
            import java.lang.IllegalStateException
            import java.io.IOException

            class MyKotlinClass {
                
                fun kotlinMethod() {
                    MyJavaClass().javaMethod()
                }
                
            }
        """
        ).indented()

        val lintResult = lint()
            .files(javaFile, kotlinFile)
            .run()

        lintResult.expectErrorCount(1)
            .expect("""
                src/com/brokoli/lint/MyKotlinClass.kt:9: Error: Kotlin code is calling Java method which throws checked exceptions. Be aware to handle it accordingly! [KotlinShouldHandleJavaExceptionsDetector]
                        MyJavaClass().javaMethod()
                        ~~~~~~~~~~~~~~~~~~~~~~~~~~
                1 errors, 0 warnings
            """.trimIndent())
    }

}