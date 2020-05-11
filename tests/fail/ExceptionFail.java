package fail;

import java.lang.System;
import java.lang.Exception;

public class ExceptionFail {

    public static void f() throws ExceptionA {
      throw new ExceptionB();
    }

    public static void main(String[] args) {
        try {
            f();
        } catch (ExceptionA a) {

        }

    }
}

class ExceptionA extends Exception {

}

class ExceptionB extends Exception {

}
