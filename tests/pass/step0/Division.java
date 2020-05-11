package pass.step0;

import java.lang.System;

public class Division {

    public static int divide(int x, int y){
        return x / y;
    }
    public static double divide(double x, double y) { return x / y; }

    public static int remainder(int x, int y){
        return x % y;
    }
    public static double remainder(double x, double y){
        return x % y;
    }

    public static void main(String[] args) {
        double ad = 4.0;
        double bd = 2.0;

        int a = divide(4,2);
        int b = remainder(a,3);

        double c = divide(ad, bd);
        double d = remainder(ad, bd);

        System.out.println("a: "+ a + "\tb: " + b);
    }
}
