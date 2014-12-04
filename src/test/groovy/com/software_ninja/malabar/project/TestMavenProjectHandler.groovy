import static net.java.quickcheck.generator.PrimitiveGenerators.*
import static net.java.quickcheck.generator.CombinedGenerators.*
import static net.java.quickcheck.QuickCheck.*
import static net.java.quickcheck.generator.iterable.Iterables.*

import net.java.quickcheck.Generator;
import net.java.quickcheck.StatefulGenerator;
import net.java.quickcheck.collection.Pair;

import org.junit.Test;

import static org.junit.Assert.*

import com.software_ninja.malabar.project.MavenProjectHandler;
import groovy.io.FileType

class MavenProjectTester {


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
  
  def pomGenerator (rootPath, pattern) { 
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
  
  /**
   * 
   */
  @Test
  void testExpandFile() {
    MavenProjectHandler mph = new MavenProjectHandler();
    String home = System.getProperty("user.home");
    for (Pair p : toIterable( pairs( characters("~./asdfghghkyiu456") ,nonEmptyStrings()))) {
        String s = "" + p.getFirst() + p.getSecond();
        if(s.startsWith("~")) {
            assertEquals(home + s.substring(1), mph.expandFile(s));
        }
        else {
            assertEquals(s, mph.expandFile(s));
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
          Map map = new MavenProjectHandler().projectInfo(pair.getFirst(),
                                                          pom.absolutePath);
          assertNotNull( map['test']);
          assertNotNull( map['runtime']);
          
          assertTrue( map['test']['classpath'].size() >= 0);
      }
  }
}
