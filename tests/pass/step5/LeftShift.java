package pass;

import java.lang.Integer;
import java.lang.System;

public class LeftShift {
    public static int leftShift(int x, int y) {
        return x << y;
    }

    public static void main(String[] args) {
        int a = 8;
        int b = 2;
        System.out.println(a + " << " + b + " = " + leftShift(a, b));
    }
}
