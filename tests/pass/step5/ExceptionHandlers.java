package pass.step5;

import java.lang.Exception;
import java.lang.System;

public class ExceptionHandlers {
    private static void f() throws Exception {
        String test = "test";
        int a = 42;
        throw new Exception();
    }

    public static void main(String[] args) {
        System.out.println("før try");
        try {
           //     f();
            throw new Exception("hej");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ;
        }
    }
}
