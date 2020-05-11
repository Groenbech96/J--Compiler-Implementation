package pass.step5;
import java.lang.System;

public class Operators {

    public static double div(double a, double b) {
        return a / b;
    }
    public static double divAsign(double a, double b) {
        a /= b;
        return a;
    }

    public static double add(double a, double b) {
        return a + b;
    }
    public static double addAsign(double a, double b) {
        a += b;
        return a;
    }

    public static double incPos(double a) {
        return a++;
    }
    public static double incPre(double a) {
        return ++a;
    }

    public static double decPos(double a) {
        return a--;
    }
    public static double decPre(double a) {
        return --a;
    }

    public static double sub(double a, double b) {
        return a - b;
    }
    public static double subAsign(double a, double b) {
        a -= b;
        return a;
    }

    public static double mult(double a, double b) {
        return a * b;
    }
    public static double multAsign(double a, double b) {
        a *= b;
        return a;
    }

    public static double rem(double a, double b) {
        return a % b;
    }
    public static double remAsign(double a, double b) {
        a %= b;
        return a;
    }

    public static double equal(double a, double b) {
        return (a == b) ? a : b;
    }
    public static double notEqual(double a, double b) {
        return (a != b) ? a : b;
    }
    public static double ge(double a, double b) {
        return (a >= b) ? a : b;
    }
    public static double gt(double a, double b) {
        return (a > b) ? a : b;
    }
    public static double lt(double a, double b) {
        return (a < b) ? a : b;
    }
    public static double le(double a, double b) {
        return (a <= b) ? a : b;
    }

    public static void main(String[] args) {
        int x = 100;
        x -= 1;
        x %= 7;
        boolean y = x >= 10 || false;
        int z = y ? 2 : 0;

        int a = 5;
        int b = 5;

        // Tests parsing for all operators
        a = !(a == b) ? a : b;
        a = ~a;
        a = (a != b) ? a : b;
        a = a / b;
        a /= b;
        a = a + b;
        a += b;
        a++;
        ++a;
        a = a - b;
        a -= b;
        a--;
        --a;
        a = a * b;
        a *= b;
        a = a % b;
        a %= b;
        a = a >> b;
        a >>= b;
        a = a << b;
        a <<= b;
        a = a >>> b;
        a >>>= b;
        a = (a >= b) ? a : b;
        a = (a > b) ? a : b;
        a = (a < b) ? a : b;
        a = (a <= b) ? a : b;
        a = a ^ b;
        a ^= b;
        a = a | b;
        a |= b;
        a = (true || false) ? a : b;
        a = a & b;
        a &= b;
        a = (true && false) ? a : b;

        int[] A = {1, 2, 3};
        System.out.flush();
    }
}
