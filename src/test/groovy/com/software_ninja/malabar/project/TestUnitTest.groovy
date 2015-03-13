
package com.software_ninja.malabar.project;

import org.junit.Test;
import org.junit.Before;

import static org.junit.Assert.*

import com.software_ninja.malabar.MalabarUtil;
import com.software_ninja.malabar.project.MavenProjectHandler;


public class TestUnitTestImpl {

  private Map config;
  private MavenProjectHandler mph;
  private String defaultRepo =  System.getProperty("user.home") +  "/.m2/repository";
  private String pm = 'maven';

  @Before
  public void init() {
    config = [ cache : [:] ];
    mph = new MavenProjectHandler(config);
  }

  @Test
  public void testUnitTest() {
    String simple = 'src/test/resources/projects/simple/';
    String scriptIn = simple + '/src/test/java/com/software_ninja/test/project/AppTest.java';
    String pmfile = simple + "pom.xml";
    String method = "testApp";
    println String.format( 'http://localhost:4429/test/?pm=%s&pmfile=%s&script=%s&&method=%s',
			   pm, pmfile, scriptIn, method);
    println mph.unitTest (defaultRepo, pm, pmfile, scriptIn, null, "java");
    println mph.unitTest (defaultRepo, pm, pmfile, scriptIn, method, "java");
  }

}


