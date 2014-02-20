package org.grumblesmurf.malabar;

import java.util.Arrays;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;

public class InvokerMvnServerTest {

    private String basicProjectDir = "src/test/project/basic";
    private String mavenHome = "c:/Users/Smith/opt/apache-maven-3.1.1";

    @Before
    public void setup () {
        System.setProperty("maven.home", mavenHome);
    }

    @Test
    public void testBasicProject() throws Exception {
        InvokerMvnServer mvnServer =  new InvokerMvnServer(basicProjectDir, Arrays.asList("compile"));
        assertTrue(mvnServer.run());
    }
    
    @Test
    public void testBasicProjectBadLifecycle() throws Exception {
        InvokerMvnServer mvnServer =  new InvokerMvnServer(basicProjectDir, Arrays.asList("compilexxx"));
        assertFalse(mvnServer.run());
    }
}
