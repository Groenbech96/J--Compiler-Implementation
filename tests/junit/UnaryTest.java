package junit;

import junit.framework.TestCase;
import pass.step5.UnaryPlus;

public class UnaryTest extends TestCase {
    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testUnary() {
        assertEquals(UnaryPlus.unaryPlus(300), 300);
        assertEquals(UnaryPlus.unaryPlus(300.0), 300.0);
        assertEquals(UnaryPlus.unaryMinus(300), -300);
        assertEquals(UnaryPlus.unaryMinus(300.0), -300.0);
    }
}
