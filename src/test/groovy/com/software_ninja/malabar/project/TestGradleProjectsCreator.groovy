package com.software_ninja.malabar.project;

import org.junit.Test;
import org.junit.Before;

import static org.junit.Assert.*

public class TestGradleProjectsCreator {
  GradleProjectsCreator target = new GradleProjectsCreator();

  @Test
  void  testArgs(){
    println System.getProperty ('user.dir');
    target.create("", ".");
    
  }
  
}
