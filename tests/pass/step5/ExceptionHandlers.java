package pass.step5;
import java.lang.Exception;

public class ExceptionHandlers {
    private static void f() throws Exception {
        throw new Exception();
    }
    
    public static void main(String[] args) {
        // TODO: Compilation fails with:  main([Ljava/lang/String;)V: Unable to resolve exception handler label(s)
        // try {
        //     f();
        // }
        // catch (Exception e) { ; }
        // finally { ; }
    }
}
