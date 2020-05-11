package pass.step3;
import pass.SomeException;

import java.lang.Math;
import java.lang.System;

interface APass {
    public double f(double x) throws SomeException;
}

class BPass implements APass {
    public double f(double x) throws SomeException {
        if (x > 0) { throw new SomeException(); }
        x -= 1.0;
        x *= 2.0;
        x /= 3.0;
        return Math.PI * --x * x++;
    }
}

public class PassTests {
    
    public static void main(String[] args) {
        int x = Integer.parseInt(args[0]);
        try {
            BPass b = new BPass();
            b.f(x > 3 ? x : 5 * x);
        } catch (SomeException e) {
            System.out.println("Error!");
        }
        finally {
            int[] a = new int[10];
            for (int i = 1; i <= a.length; i++) {
                a[i] = i;
            }
            for (int i : a) {
                System.out.println("Done " + i + "!");
            }
        }
    }
}
