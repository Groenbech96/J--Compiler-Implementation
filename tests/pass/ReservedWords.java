package pass;
import java.lang.Exception;


public class ReservedWords extends SomeClass implements SomeInterface {
    //TODO: Uncomment stuff as they are added for parsing support
    public static void main(String[] args) {
        // do {
        //     /* do nothing */
        // } 
        while (4==4) {
            if(true){
                break;
            }
        }
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
