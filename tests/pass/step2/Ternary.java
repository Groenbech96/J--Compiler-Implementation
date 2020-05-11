package pass.step2;

public class Ternary {
    public static int ternary(boolean b, int x, int y) { return b ? x : y; }

    public static void main(String[] args) {
        boolean b = true;
        int x = -5;
        int y = -8;
        System.out.println(b + " ? " + x + " : " + y + " = " + ternary(b, x, y));
    }
}