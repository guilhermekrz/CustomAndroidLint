package com.brokoli.lint;

class JavaClass {

    public void methodDoesNotThrow()  {

    }

    public void methodThrowsCheckedException() throws IllegalAccessException {
        throw new IllegalAccessException();
    }

    public void methodThrowsUncheckedException() {
        throw new IllegalStateException();
    }

}
