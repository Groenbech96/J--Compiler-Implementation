package pass;
import java.lang.Exception;

public class SomeClass{

}
public interface SomeInterface {

}
public class SomeException extends Exception{

}

public class ReservedWords extends SomeClass implements SomeInterface {
    //TODO: Uncomment stuff as they are added for parsing support
    public static void main(String[] args) {
        // do {
        //     /* do nothing */
        // } 
        while (5==4);
        for (;;) {
            try {
                // if (true) { continue; }
                throw new SomeException();
            }
            catch (SomeException e) {
                /* do nothing */
            }
        }

        // final int x;
    } 
}
