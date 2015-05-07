package com.software_ninja.malabar.project;

import org.junit.Test;
import org.junit.Before;

import static org.junit.Assert.*

public class TestGradleProjectsCreator {
  GradleProjectsCreator target = new GradleProjectsCreator();

  @Test
  void  testArgs() throws Exception {
    println "USER.DIR" + System.getProperty ('user.dir');
    def proj = target.create("", ".");
    println "GRADLE PROJECT:" + proj;
    Class c = proj[0].getClass();
    Class[] theInterfaces = c.getInterfaces();
    for (int i = 0; i < theInterfaces.length; i++) {
      String interfaceName = theInterfaces[i].getName();
      System.out.println( "INTERFACE:" + interfaceName);
    }
    println "DEPENDENCIES: " + target.resolveDependencies(  proj[0],  "", "runtime") ;
    
  }
  
}
