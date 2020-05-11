package junit;

import junit.framework.TestCase;
import pass.step5.Operators;

public class OperatorsTest extends TestCase {

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void testOperatorsDouble() {

        double a = 5.0;
        double b = 2.0;

        this.assertEquals(7.0, Operators.add(a,b));
        this.assertEquals(7.0, Operators.addAsign(a,b));
        this.assertEquals(2.5, Operators.div(a,b));
        this.assertEquals(2.5, Operators.divAsign(a,b));

        this.assertEquals(6.0, Operators.incPos(a));
        this.assertEquals(6.0, Operators.incPre(a));
        this.assertEquals(4.0, Operators.decPos(a));
        this.assertEquals(4.0, Operators.decPre(a));

        this.assertEquals(2.5, Operators.sub(a,b));
        this.assertEquals(2.5, Operators.subAsign(a,b));
        this.assertEquals(10.0, Operators.mult(a,b));
        this.assertEquals(10.0, Operators.multAsign(a,b));

        this.assertEquals(1.0, Operators.rem(a,b));
        this.assertEquals(1.0, Operators.remAsign(a,b));

        this.assertEquals(b, Operators.equal(a,b));
        this.assertEquals(a, Operators.notEqual(a,b));

        this.assertEquals(a, Operators.ge(a,b));
        this.assertEquals(a, Operators.gt(a,b));

        this.assertEquals(b, Operators.le(a,b));
        this.assertEquals(b, Operators.lt(a,b));

    }




}
