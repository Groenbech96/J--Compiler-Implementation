// Copyright 2013 Bill Campbell, Swami Iyer and Bahar Akbal-Delibas
package junit;


import junit.framework.Assert;
import junit.framework.TestCase;
import pass.Classes;

public class ClassesTest extends TestCase {

    public void testMessage() {
        assertEquals(Classes.message(), "Hello, World!");
    }
}