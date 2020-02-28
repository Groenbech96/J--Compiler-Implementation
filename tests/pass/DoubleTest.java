package pass;

import java.lang.System;

public class DoubleTest {

    public static double getDouble(double d){
        return 0.0;
    }

    public static void main(String[] args) {
        int i = 1;
        double a = 5.;
        double b = 0.7;
        double c = 5.7;
        double castI = (double) c;
        double implicitI = i;
        double fun = getDouble(0.0);

        System.out.println("a: "+ castI + "\tb: " + implicitI);
    }
}
