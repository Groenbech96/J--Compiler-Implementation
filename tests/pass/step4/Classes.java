// Copyright 2013 Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package pass.step4;

import java.lang.System;

public class Classes {

    public static String message() {
        return ClassA.a + ", " + (new ClassB()).b;
    }

    public static void main(String[] args) {
        System.out.println(Classes.message());
    }

}

class ClassA {

    public static String a = "Hello";

}

class ClassB {

    public String b = "World!";

}
