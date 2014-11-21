
//groovy.grape.Grape.grab([group:'com.jcabi', module:'jcabi-aether', version:'0.10.1'])
@Grapes([
         @Grab(group='org.apache.maven', module='maven-core', version='3.0.5'),
         @Grab(group='com.jcabi', module='jcabi-aether', version='0.10.1')
         ])


//@Grab(group='org.springframework', module='spring', version='2.5.6')
//import org.springframework.jdbc.core.JdbcTemplate

//class AA{}

import com.jcabi.aether.Aether;
import java.io.File;
import java.util.Arrays;
import org.apache.maven.project.MavenProject;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.util.artifact.DefaultArtifact;

File local = new File("/tmp/local-repository");
    Collection<RemoteRepository> remotes = Arrays.asList(
      new RemoteRepository(
        "maven-central",
        "default",
        "http://repo1.maven.org/maven2/"
      )
    );

Collection<Artifact> deps = new Aether(remotes, local).resolve(
      new DefaultArtifact("junit", "junit-dep", "", "jar", "4.10"),
      "runtime"
    );

println deps.size()
println deps.get(0).getFile()
println deps.get(1).getFile()
