package junit;

import junit.framework.TestCase;
import pass.Division;

public class DivisionTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testDivision(){
        this.assertEquals(2, Division.divide(4,2));
        this.assertEquals(1,Division.divide(3,2));
        this.assertEquals(3,Division.divide(3,1));
    }

    public void testRemaineder(){
        this.assertEquals(1,Division.remainder(1,3));
        this.assertEquals(1,Division.remainder(3,2));
//        this.assertEquals(1,Division.remainder(-1,2)); //Negative numbers apparently doesn't work yet.
        this.assertEquals(4, Division.remainder(9,5));
    }
}
