package pass;

public class UnaryPlus {
    public static int unaryPlus(int x) { return +x; }

    public static void main(String[] args) {
        int a = -5;
        System.out.println("+(" + a + ") = " + unaryPlus(a));
    }
}
