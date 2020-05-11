package pass.step5;
import java.lang.System;

public class UnaryPlus {
    public static int unaryPlus(int x) { return +x; }
    public static double unaryPlus(double x) { return +x;}
    public static int unaryMinus(int x) { return -x; }
    public static double unaryMinus(double x) { return -x;}


    public static void main(String[] args) {
        int a = -5;
        System.out.println("+(" + a + ") = " + unaryPlus(a));
    }
}
