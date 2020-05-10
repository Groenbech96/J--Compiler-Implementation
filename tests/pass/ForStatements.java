package pass;

import java.lang.System;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

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

        // For loop with initial statements instead of variable declarations
        int i = 0;
        for (i += 1, i += 1, i += 1; i == 0; i += 1) {
        }

        List list = new List(a);

        for (Object value : list) {
        }

        TestClass[] arr = new TestClass[5];
        for (TestClass tc : arr) {
        }

        for (; ; ) {
        }
    }
}

class List implements Iterable{

    public List(int[] elements){
        this.elements = elements;
        this.currentElement = 0;
    }

    int[] elements;
    int currentElement;

    public Iterator iterator() {
        return Arrays.stream(this.elements).iterator();
    }

    public void forEach(Consumer action) {
        for(int element : elements){
            action.accept(element);
        }
    }

    public Spliterator spliterator() {
        return null;
    }
}

class TestClass {
}