package pass.step4;
import java.lang.Exception;

public class ExceptionHandlers {
    private static void f() throws Exception {
        throw new Exception();
    }
    
    public static void main(String[] args) {
        try {
            f();
        }
        catch (Exception e) { ; }
        finally { ; }
    }
}
