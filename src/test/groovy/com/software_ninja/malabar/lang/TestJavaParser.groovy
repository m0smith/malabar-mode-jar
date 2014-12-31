package com.software_ninja.malabar.lang;

import org.junit.Test;
import static org.junit.Assert.*; 

import com.software_ninja.malabar.lang.JavaParser;
 
public class TestJavaParser {

  def classloader = new GroovyClassLoader();
  def javaParser = new JavaParser(classloader);

  @Test
  public void testFileParser() throws Exception {
    String simple = 'src/test/resources/projects/simple/';
    String scriptIn = simple + '/src/test/java/com/software_ninja/test/project/AppTest.java';
    String pm = simple + "pom.xml";


    assertEquals(String.class, javaParser.parse(new File(scriptIn))['class']);
  }

}
