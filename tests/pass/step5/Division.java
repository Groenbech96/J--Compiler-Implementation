package pass.step5;

import java.lang.System;

public class Division {

    public static int divide(int x, int y){
        return x / y;
    }

    public static int remainder(int x, int y){
        return x % y;
    }

    public static void main(String[] args) {
        int a = divide(4,2);
        int b = remainder(a,3);
        System.out.println("a: "+ a + "\tb: " + b);
    }
}
