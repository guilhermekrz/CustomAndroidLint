package com.fabiocarballo.lint

@Suppress("unused")
class ClassThatDoesNotHandleThrows {

    fun method() {
        ClassThatThrows().methodThrows()
    }

}