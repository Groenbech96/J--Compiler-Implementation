package pass;

import java.lang.System;

public class DoubleTest {

    public static void main(String[] args) {
        int i = 1;
        double a = 5.;
        double b = 0.7;
        double c = 5.7;
        double castI = (double) c;
        double implicitI = i;

        System.out.println("a: "+ castI + "\tb: " + implicitI);
    }
}
