package com.ll.rsv.standard.base;

import org.springframework.stereotype.Component;

@Component
public class Foo {
    private final String name;

    public Foo() {
        this.name = "foo " + Math.random();
    }

    public String toString() {
        return this.name;
    }
}
