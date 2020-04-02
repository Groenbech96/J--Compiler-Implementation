package pass;

public class Operators {
    public static void main(String[] args) {
        int a = 5;
        int b = 5;

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
