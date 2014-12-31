package com.software_ninja.test.project;

import  com.software_ninja.test.project.App;
//import  com.software_ninja.test.project.App2;

import org.junit.Test;
import static org.junit.Assert.*; 

/**
 * Unit test for simple App.
 */
public class AppTest  
{
    /**
     * Rigourous Test :-)
     */

    App app = new App();
    
    @Test
    public void testApp()
    {
        assertTrue( true );
    }

    @Test
    public void failApp()  
    {
        fail( "This always fails" );
    }

    @Test
    public void testBoolean() throws Exception {
	assertEquals(true, app.getTrue());
    }
}
