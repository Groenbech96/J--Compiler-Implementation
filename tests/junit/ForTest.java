package junit;

import junit.framework.TestCase;

public class ForTest extends TestCase {
    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testFor(){
        for(int i = 0; i <= 100; i += 1) {
            i += 1;
        }

        int[] a = {1, 2, 3};
        for(int i : a) {
            i = 1;
        }
    }

}
