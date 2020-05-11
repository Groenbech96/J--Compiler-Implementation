package pass.step0;

import java.lang.System;

public class ExclusiveOr {
    public static int exclusiveOr(int x, int y) {
        return x ^ y;
    }

    public static void main(String[] args) {
        int a = 4646;
        int b = 8484;
        System.out.println(a + " ^ " + b + " = " + exclusiveOr(a, b));
    }
}
