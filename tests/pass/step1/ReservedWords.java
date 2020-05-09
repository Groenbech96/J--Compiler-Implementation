public class ReservedWords extends SomeClass implements  SomeInterface {
    public static void main(String[] args) {
        do {
            /* do nothing */
        } while (true);
        for (;;) {
            try {
                if (true) { continue; }
            }
            catch (SomeException e) {
                /* do nothing */
            }
        }
        final int x;
    } 
}
