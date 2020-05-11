package pass;
import java.lang.Double;

public class DoubleTest {
    // public static double func(double d) {
    //     return 0.0;
    // }

    public static void main(String[] args) {
        int i = 1;
        Double boxed = new Double(5.0);
        double raw = 1.0;
        raw += .0;
        raw += 5.;
        raw += (double)boxed;
        raw += (double)i;
        raw = raw + (double)i + (double)boxed;
        double[] arr = new double[5];
        raw = arr[2];

        double a = 3.0;
        double b = 4.0;

        // Tests parsing for all operators
        a = !(a == b) ? a : b;
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

        a = (a >= b) ? a : b;
        a = (a > b) ? a : b;
        a = (a < b) ? a : b;
        a = (a <= b) ? a : b;

        a = (true || false) ? a : b;
        a = (true && false) ? a : b;



    }
}
