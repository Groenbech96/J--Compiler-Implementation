package pass;

public class DoubleTest {

    public static double func(double d) {
        return 0.0;
    }

    public static void main(String[] args) {
        int i = 1;
        Double boxed = new Double(5.0);
        double raw = 1.0;
        raw += .0;
        raw += 5.;
        raw += boxed;
        raw += i;
        raw = raw + i + boxed;
    }

}
