package com.software_ninja.malabar.lang;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*; 

import java.util.logging.Level;

import com.software_ninja.malabar.MalabarUtil;
import com.software_ninja.malabar.project.MavenProjectHandler;
 
public class TestGroovyParser {

  String simple = 'src/test/resources/projects/simple/';
  String scriptIn = simple + '/src/test/java/com/software_ninja/test/project/AppTest.java';
  String pmfile = simple + "pom.xml";
  String pm = 'maven';
  String repo = "~/.m2/repository";
  
  def mavenProjectHandler = new MavenProjectHandler([cache:[:]]);
  def cacheEntry = mavenProjectHandler.lookInCache( pm, pmfile, { mavenProjectHandler.fecthProjectInfo(repo, pm, pmfile)});
  
  
  def groovyParser = cacheEntry['parsers']['groovy'];
  
  @Before
  public void before() {
    MalabarUtil.setLevel( "com.software_ninja.malabar.lang.GroovyParser", Level.FINEST);
  }
 
  @Test
  public void testFileParser() throws Exception {
  

    assertEquals([], groovyParser.parse(new File(scriptIn))['errors']);
    assertEquals("com.software_ninja.test.project.AppTest", groovyParser.parse(new File(scriptIn))['class'].getName());
  }
  @Test
  public void testStringParser() throws Exception {
    String code = "class HamsterTest {} ";

    assertEquals([], groovyParser.parse(code)['errors']);
    assertEquals("HamsterTest", groovyParser.parse(code)['class'].getName());
  }

}
