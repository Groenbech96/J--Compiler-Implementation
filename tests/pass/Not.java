package pass;

import java.lang.System;

public class Not {
    public static int not(int x) {
        return ~x;
    }

    public static void main(String[] args) {
        int a = 42;
        System.out.println("~" + a + " = " + not(a));
    }
}
