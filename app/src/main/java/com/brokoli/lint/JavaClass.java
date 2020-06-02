package com.brokoli.lint;

import java.util.List;

class JavaClass {

    public void methodDoesNotThrow()  {

    }

    public void methodThrowsCheckedException() throws IllegalAccessException {
        throw new IllegalAccessException();
    }

    public void methodThrowsUncheckedException() {
        throw new IllegalStateException();
    }

    public void methodChangesList(List<Object> objects) {
        objects.add(new Object());
    }

}
