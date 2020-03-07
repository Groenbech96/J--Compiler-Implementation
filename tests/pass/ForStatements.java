package pass;

import java.lang.System;

public class ForStatements {
    public static void main(String[] args) {
        int sum1 = 0, sum2 = 0;
        for (int i = 1; i <= 10; i += 1) {
            sum1 += i;
        }
        int[] a = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        for (int i : a) {
            sum2 += i;
        }
        System.out.println(sum1 == sum2);
    }
}
