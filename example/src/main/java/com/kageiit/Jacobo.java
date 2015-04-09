package com.kageiit;

public class Jacobo {

    public void foo() {
        if (bar()) {
            foo();
        } else {
            bar();
        }
    }

    public boolean bar() {
        return false;
    }
}
