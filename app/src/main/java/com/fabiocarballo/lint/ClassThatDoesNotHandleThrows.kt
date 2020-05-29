package com.fabiocarballo.lint

class ClassThatDoesNotHandleThrows {

    fun method() {
        ClassThatThrows().methodThrows()
    }

}