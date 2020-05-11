package pass.step0;

import java.lang.System;

public class And {
    public static int and(int x, int y) {
        return x & y;
    }

    public static void main(String[] args) {
        int a = 4646;
        int b = 8484;
        System.out.println(a + " & " + b + " = " + and(a, b));
    }
}
