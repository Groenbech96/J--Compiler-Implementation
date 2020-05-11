package pass.step5;

import java.lang.Exception;
import java.lang.System;

public class ExceptionHandlers {
    private static void f() throws Exception {
        System.out.println("Inside method, before exception is thrown!");
        throw new Exception();
    }

    public static void main(String[] args) {
        System.out.println("før try");
        try {
            f();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("Finally!");
        }
    }
}
