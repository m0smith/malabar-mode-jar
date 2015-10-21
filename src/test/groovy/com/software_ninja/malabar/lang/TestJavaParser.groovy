
package com.software_ninja.malabar.lang;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*; 
import com.software_ninja.malabar.MalabarUtil;
import com.software_ninja.malabar.project.MavenProjectHandler;
import java.util.logging.Level;
 
public class TestJavaParser {

  String simpleTestPath =
      "projects/simple/src/test/java/com/software_ninja/test/project/";
  String simpleSrcPath =
      "projects/simple/src/main/java/com/software_ninja/test/project/";
      
  String scriptIn =
      TestJavaParser.class.getClassLoader().getResource(simpleTestPath + "AppTest.java").getPath();
  String errorScriptIn =
      TestJavaParser.class.getClassLoader().getResource(simpleSrcPath + "ParserTargetWithError.java").getPath();
  String pmfile =
      TestJavaParser.class.getClassLoader().getResource("projects/simple/pom.xml").getPath();
    
  String pm = 'maven';
  String repo = "~/.m2/repository";
  
  private static MavenProjectHandler mph = new MavenProjectHandler([cache:[:]]);
  def cacheEntry = mph.lookInCache(pm, pmfile, { mph.fecthProjectInfo(repo, pm, pmfile)});
  def javaParser = cacheEntry['parsers']['java'];
 
  @Before
  public void before() {
    MalabarUtil.setLevel("com.software_ninja.malabar.lang.JavaParser", Level.FINEST);
  }

  @Test
  public void testFileParser() throws Exception {
  
    def rtnval = javaParser.parse(new File(scriptIn));
    assertEquals([], rtnval['errors']);

    // FIXME: This is not implemented well or can't...
    //assertNotNull(rtnval['class']);
    //assertEquals("com.software_ninja.test.project.AppTest", rtnval['class'].getName());
  }

  @Test
  public void testStringParser() throws Exception {
    String code = "class HamsterTest {} ";
    assertEquals([], javaParser.parse(code)['errors']);
    // FIXME: Can't read it !
    //assertEquals("HamsterTest", javaParser.parse(code)['class']);
  }

  @Test
  public void testFileParserWithError() throws Exception {
  
    def rtnval = javaParser.parse(new File(errorScriptIn));
    //assertEquals( new File(errorScriptIn).getAbsolutePath(), new File(rtnval['errors'][0]['sourceLocator']).getAbsolutePath());
    assertNull(rtnval['class']);
  }




}
