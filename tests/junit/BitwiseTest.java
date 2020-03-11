package junit;

import junit.framework.TestCase;
import pass.And;
import pass.ExclusiveOr;
import pass.InclusiveOr;
import pass.Not;

public class BitwiseTest extends TestCase {
    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testBitwiseOps() {
        testAnd();
        testExclusiveOr();
        testInclusiveOr();
        testNot();
    }

    public void testAnd() {
        assertEquals(And.and(150, 300), 4);
        assertEquals(And.and(-100, 300), 268);
    }

    public void testExclusiveOr() {
        assertEquals(ExclusiveOr.exclusiveOr(150, 300), 442);
        assertEquals(ExclusiveOr.exclusiveOr(-100, 300), -336);
    }

    public void testInclusiveOr() {
        assertEquals(InclusiveOr.inclusiveOr(150, 300), 446);
        assertEquals(InclusiveOr.inclusiveOr(-100, 300), -68);
    }

    public void testNot() {
        assertEquals(Not.not(150), -151);
        assertEquals(Not.not(0), -1);
    }
}
