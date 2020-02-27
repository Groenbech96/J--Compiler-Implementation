package pass;

import java.lang.System;

public class NegationAndPositive {
    public static int positive(int x, int y) {
        return +x + y;
    }

    public static int negation(int x, int y) {
        return -x - y;
    }

    public static void main(String[] args) {
        int a = positive(4, 2);
        int b = negation(a, 3);
        System.out.println("a: " + a + "\tb: " + b);
    }
}
