
package com.software_ninja.malabar.project;

import org.junit.Test;
import org.junit.Before;

import static org.junit.Assert.*

import com.software_ninja.malabar.MalabarUtil;
import com.software_ninja.malabar.project.MavenProjectHandler;


public class TestExecImpl {

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
  public void testExec() {
    String simple = 'src/test/resources/projects/simple/';
    String scriptIn = simple + '/src/test/java/com/software_ninja/test/project/App.java';
    String pmfile = simple + "pom.xml";

    String clazzName = "com.software_ninja.test.project.App";
    //println 'http://localhost:4429/exec/?pm=' + pm +'&class=' + clazzName + "&repo=" + defaultRepo;
    mph.parse(defaultRepo, pm, pmfile, scriptIn, null, "java");
    mph.exec (defaultRepo, pm, pmfile, clazzName, null); 
    mph.exec (defaultRepo, pm, pmfile, clazzName, ["a", "b", "c"] as String[]);
  }

}


