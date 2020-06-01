package com.brokoli.lint

import java.io.IOException

@Suppress("unused")
class KotlinClass {

    fun methodDoesNotThrow() {
        JavaClass().methodDoesNotThrow()
    }

    @Throws(IOException::class)
    fun annotatedMethodThrowsCheckedException() {
        JavaClass().methodThrowsCheckedException()
    }

    fun notAnnotatedMethodThrowsCheckedException() {
        JavaClass().methodThrowsCheckedException()
    }

    fun notAnnotatedMethodCatchesCheckedException() {
        try {
            JavaClass().methodThrowsCheckedException()
        } catch (e: IllegalAccessException) {

        }
    }

    @Throws(IOException::class)
    fun annotatedMethod() {
    }

    @Throws(IOException::class)
    fun annotatedethodThrowsUncheckedException() {
        JavaClass().methodThrowsUncheckedException()
    }

    fun notAnnotatedethodThrowsUncheckedException() {
        JavaClass().methodThrowsUncheckedException()
    }

}