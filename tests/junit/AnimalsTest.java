package junit;

import junit.framework.TestCase;
import pass.step5.Animalia;
import pass.step5.Division;

public class AnimalsTest extends TestCase {


    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testAnimals() {
        this.assertEquals("Drosophila melanogaster",Animalia.getFly());
        this.assertEquals("Panthera tigris corbetti",Animalia.getTiger());
    }

}
