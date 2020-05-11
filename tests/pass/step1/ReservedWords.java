package pass.step1;

import pass.SomeClass;
import pass.SomeException;
import pass.SomeInterface;

public class ReservedWords extends SomeClass implements SomeInterface {
    public static void main(String[] args) {
        do {
            /* do nothing */
        } while (false);
        final int x;
        for (;;) {
            try {
                if (true) { continue; }
                throw new SomeException();
            }
            catch (SomeException e) {
                /* do nothing */
            }
        }

    } 
}
