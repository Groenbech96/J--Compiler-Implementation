package junit;

import junit.framework.TestCase;
import pass.step5.LeftShift;
import pass.step5.RightShift;

public class ShiftTest extends TestCase {

    private RightShift rShift;
    private LeftShift lShift;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        rShift = new RightShift();
        lShift = new LeftShift();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testShift() {

    }

}
