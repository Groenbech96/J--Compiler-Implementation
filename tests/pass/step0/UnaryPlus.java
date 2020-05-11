package pass.step0;

public class UnaryPlus {

    public static int unaryPlus(int x) { return +x; }
    public static double unaryPlus(double x) { return +x; }

    public static void main(String[] args) {
        int a = -5;
        System.out.println("+(" + a + ") = " + unaryPlus(a));

        double b = -5.0;
        System.out.println("+(" + b + ") = " + unaryPlus(b));
    }
}
