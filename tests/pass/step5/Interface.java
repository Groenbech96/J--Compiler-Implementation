package pass.step5;

/**
 * Interface.java
 */
interface A {
    public int f(int x);
    static int i = 5;
}

interface X {
    int g(int x);
}

interface Y extends A, X {
    int h(int x);
}

class B implements A {
    public int f(int x) {
	    return x * x;
    }
}

class C implements X {
    public int f(int x) {
        return 5;
    }

    public int g(int x) {
	    return 3;
    }
}

public class Interface {
    public static void main(String[] args) {
        int x = 5;
        B b = new B();
        C c = new C();
        int y = b.f(x);
        int z = c.f(x);
    }
}
