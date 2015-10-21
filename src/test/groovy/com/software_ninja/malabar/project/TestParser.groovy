
package com.software_ninja.malabar.project;

import org.junit.Test;
import org.junit.BeforeClass;

import static org.junit.Assert.*

import com.software_ninja.malabar.MalabarUtil;
import com.software_ninja.malabar.project.MavenProjectHandler;


public class TestParserImpl {

  private static MavenProjectHandler mph;
  private static Map config;
  private String defaultRepo = System.getProperty("user.home") +  "/.m2/repository";
  private String pm = 'maven';

  @BeforeClass
  public static void init() {
    config = [ cache : [:] ];
    mph = new MavenProjectHandler(config);
  }

  @Test
  public void testParser() {
    String simple = 'src/test/resources/projects/simple/';
    String scriptIn = simple + '/src/test/java/com/software_ninja/test/project/AppTest.java';
    String pmfile = simple + "pom.xml";
    String method = "testApp";

    //println 'http://localhost:4429/parse/?pm=' + pm +'&script=' + scriptIn ;
    def actual = mph.parse(defaultRepo, pm, pmfile, scriptIn, null, "groovy");
    assertEquals([], actual)

    println mph.parse(defaultRepo, pm, pmfile, scriptIn, null, "groovy-strict");
  }
}


