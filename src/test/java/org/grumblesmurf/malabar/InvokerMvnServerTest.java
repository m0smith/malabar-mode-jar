package org.grumblesmurf.malabar;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;


@RunWith(value = Parameterized.class)
public class InvokerMvnServerTest {

    private String basicProjectDir;
    private String mavenHome;

    public InvokerMvnServerTest(String basicProjectDir, String mavenHome) {
        this.basicProjectDir = basicProjectDir;
        this.mavenHome = mavenHome;
    }

    @Parameters
    public static Collection<Object[]> data() {
        Object[][] data = new Object[][] { 
            { "src/test/project/basic", "c:/Users/Smith/opt/apache-maven-3.1.1" },
            { "src/test/project/basic", "c:/Users/Smith/opt/apache-maven-3.0.5" }
        };
        return Arrays.asList(data);
    }


    @Before
    public void setup () {
        System.setProperty("maven.home", mavenHome);
    }

    @Test
    public void testBasicProject() throws Exception { 
        boolean desc = runBasicProject("compile");
        assertTrue(desc);
    }

    @Test
    public void testBasicProjectBogus() throws Exception {
        boolean desc = runBasicProject("bogus");
        assertFalse(desc);
    }

    public boolean runBasicProject(String... goals) throws Exception {
        InvokerMvnServer mvnServer =  new InvokerMvnServer(basicProjectDir);
        return mvnServer.run(basicProjectDir + "/pom.xml", false, goals).run();
    }
    
}
