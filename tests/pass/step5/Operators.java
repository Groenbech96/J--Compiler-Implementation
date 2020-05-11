package pass.step5;
import java.lang.System;

public class Operators {

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
