package pass;

public class StaticInstanceBlock {
    public static int test;
    private int nonStaticTest;
    static {
        test = 5;
    }

    //Also just testing an instance block
    {
        nonStaticTest = 3;
    }
}
