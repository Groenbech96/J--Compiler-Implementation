package pass.step0;

import java.lang.Integer;
import java.lang.System;

public class LogicalRightShift {
    public static int logicalRightShift(int x, int y) {
        return x >>> y;
    }

    public static void main(String[] args) {
        int a = 8;
        int b = 2;
        System.out.println(a + " >>> " + b + " = " + logicalRightShift(a, b));
    }
}
