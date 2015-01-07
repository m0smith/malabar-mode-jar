package com.software_ninja.malabar.project;

import static net.java.quickcheck.generator.PrimitiveGenerators.*
import static net.java.quickcheck.generator.CombinedGenerators.*
import static net.java.quickcheck.QuickCheck.*
import static net.java.quickcheck.generator.iterable.Iterables.*

import net.java.quickcheck.Generator;
import net.java.quickcheck.StatefulGenerator;
import net.java.quickcheck.collection.Triple;
import net.java.quickcheck.collection.Pair;

import org.junit.Test;
import org.junit.Before;

import static org.junit.Assert.*

import com.software_ninja.malabar.MalabarUtil
import com.software_ninja.malabar.project.MavenProjectHandler;

import groovy.io.FileType

class MavenProjectTester {

  private Map config;

  /**
   * A generator that takes a list and returns the elements in random order
   */
  def elementGenerator (coll) {
    return new StatefulGenerator<File>() {

      private Generator<Integer> ints = integers(0, coll.size() - 1);

      public void reset () {
	ints = PrimitiveGenerators.integers(0, coll.size() - 1);
      }

      public File next () {
	int i = ints.next();
	println i + " " + coll.size();
	return coll[i];
      }
    }
  }
  
  /**
   * Generator of pom files. See src/test/resources/pom.
   *
   * Returns a generator that returns the list of pom files.
   **/
  
  def pomGenerator (String rootPath, pattern) { 
    List<File> files = [];
    File root = new File(rootPath);
    root.eachFileMatch(pattern) {
      f -> files << f;
    }
    root.eachFileRecurse(FileType.DIRECTORIES) {
      dir -> dir.eachFileMatch(pattern) {
	f -> files << f;
      }
    }

    return elementGenerator(files);
    
  }
  
  @Before
  public void init() {
    config = [ cache : [:] ];
  }
  /**
   * 
   */
  @Test
  void testExpandFile() {

    String home = System.getProperty("user.home");
    for (Pair p : toIterable( pairs( characters("~./asdfghghkyiu456") ,nonEmptyStrings()))) {
      String s = "" + p.getFirst() + p.getSecond();
        if(s.startsWith("~")) {
            assertEquals(home + s.substring(1), MalabarUtil.expandFile(s));
        }
        else {
            assertEquals(s, MalabarUtil.expandFile(s));
        }
    }
  }
    

  
  /**
   * Properties:
   *  # Not throw an exception
   *  # Returns a map
   *  # the map has a runtime key
   *  # the map has a test key
   **/
  @Test
  void testMavenImport() {
      String defaultRepo =  System.getProperty("user.home") +  "/.m2/repository";
      for (Pair pair : toIterable( pairs( ensureValues(null , defaultRepo),
                                         pomGenerator("src/test/resources/pom/",~/.*\.pom/ )))) {
          File pom = pair.getSecond();
          println "processing " + pom;
          Map map = new MavenProjectHandler(config).projectInfo(pair.getFirst(),
                                                          pom.absolutePath);
          assertNotNull( map['test']);
          assertNotNull( map['runtime']);          
	  assertNotNull( map['systemProperties']);
          
          assertTrue( map['test']['classpath'].size() >= 0);
      }
  }

  @Test
  void  testResource(){

    File pomFile = new File(this.getClass().getClassLoader().getResource( "pom/jdom-1.0.pom").toURI());
    String pom = pomFile.getAbsolutePath();
    String defaultRepo =  System.getProperty("user.home") +  "/.m2/repository";
    def mph = new MavenProjectHandler(config);
    for (Triple data : toIterable( triples(integers(), booleans(), booleans()))){
      int max = data.getFirst();
      boolean isClass = data.getSecond();
      boolean useRegex = data.getThird();
      def pattern = /S*/;
      def result =  mph.resource(defaultRepo, pom, pattern, max, isClass, useRegex);
      int size = result.size();
      assertTrue( "Size should be <= than max size:" + size + " max:" + max, size <= Math.max(max, 0));
      //if( size > 0) println result.first();
    }
  }
  @Test
  void  testResourceFullClass(){

    File pomFile = new File(this.getClass().getClassLoader().getResource( "pom/jdom-1.0.pom").toURI());
    String pom = pomFile.getAbsolutePath();
    String defaultRepo =  System.getProperty("user.home") +  "/.m2/repository";
    def mph = new MavenProjectHandler(config);
    int max = 10
    boolean isClass = true;
    boolean useRegex = false;
    def pattern = "org.apache.xalan.xsltc.compiler.util.ResultTreeType"
    def result =  mph.resource(defaultRepo, pom, pattern, max, isClass, useRegex);
  }

  @Test
  void  testTags(){

    File pomFile = new File(this.getClass().getClassLoader().getResource( "pom/jdom-1.0.pom").toURI());
    String pom = pomFile.getAbsolutePath();
    String defaultRepo =  System.getProperty("user.home") +  "/.m2/repository";
    def mph = new MavenProjectHandler(config);
    def className = "org.apache.xalan.xsltc.compiler.util.ResultTreeType"
    def result =  mph.tags(defaultRepo, pom, className);
  }

  @Test
  public void testFileUnitTest() throws Exception {
    String simple = 'src/test/resources/projects/simple/';
    String scriptIn = simple + '/src/test/java/com/software_ninja/test/project/AppTest.java';
    String pm = simple + "pom.xml";
    String repo = "~/.m2/repository";
    new File(simple + "target").deleteDir();
    def mph = new MavenProjectHandler(config);
    def rtnval = mph.unitTest(repo, pm, scriptIn, null, "java");
    assertEquals("This always fails", rtnval[0][1]);
  }

}
