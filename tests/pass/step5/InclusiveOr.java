package pass.step5;

import java.lang.System;

public class InclusiveOr {
    public static int inclusiveOr(int x, int y) {
        return x | y;
    }

    public static void main(String[] args) {
        int a = 4646;
        int b = 8484;
        System.out.println(a + " | " + b + " = " + inclusiveOr(a, b));
    }
}