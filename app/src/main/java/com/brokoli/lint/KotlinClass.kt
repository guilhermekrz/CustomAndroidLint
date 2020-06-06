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

    fun checkedException() {
        throw IOException()
    }

    fun uncheckedException() {
        throw RuntimeException()
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

    fun callMethodChangesListWithMutableList() {
        JavaClass().methodChangesList(mutableListOf())
    }

    fun callMethodChangesListWithImmutableList() {
        val list = listOf<Object>()
        JavaClass().methodChangesList(list)
    }

    fun methodWith8Parameters(first: Boolean, second: String, third: Int, fourth: Long, fifth: Char, sixth: Boolean, seventh: Boolean, eight: Boolean) {

    }

}