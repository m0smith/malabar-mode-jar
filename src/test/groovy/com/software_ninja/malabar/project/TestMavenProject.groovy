import static net.java.quickcheck.generator.PrimitiveGenerators.*
import static net.java.quickcheck.generator.CombinedGenerators.*
import static net.java.quickcheck.QuickCheck.*
import static net.java.quickcheck.generator.iterable.Iterables.*

import net.java.quickcheck.Generator;

import org.junit.Test;

import static org.junit.Assert.*

import com.software_ninja.malabar.project.MavenProject;


class MavenProjectTester {

  
  
  def pomGenerator (root, pattern) { 
    List<File> files = [];
    new File(root).eachFileMatch(pattern) {
      f -> files << f;
    }
    return ensureValues(files);
    
  }
  
  
  
  @Test
  void testMavenImport() {
    for (File pom : toIterable( pomGenerator("src/test/resources/pom",~/.*\.pom/ ))) {
      new MavenProject().projectInfo( System.getProperty("user.home") +  ".m2/repository",
				      pom.absolutePath);
    }
  }
}
