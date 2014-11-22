// http://stackoverflow.com/questions/11799923/programmatically-resolving-maven-dependencies-outside-of-a-plugin-get-reposito
//http://stackoverflow.com/questions/11525318/how-do-i-obtain-a-fully-resolved-model-of-a-pom-file
// http://aether.jcabi.com/
// http://stackoverflow.com/questions/4206679/can-anyone-give-a-good-example-of-using-org-apache-maven-cli-mavencli-programatt


//package com.software_ninja.malabar;



groovy.grape.Grape.grab([group:'org.apache.maven', module:'maven-embedder', version:'3.1.0'])
groovy.grape.Grape.grab([group:'org.eclipse.aether', module:'aether-connector-wagon', version:'0.9.0.M2'])
groovy.grape.Grape.grab([group:'org.apache.maven.wagon', module:'wagon-http', version:'2.5'])
groovy.grape.Grape.grab([group:'org.sonatype.sisu', module:'sisu-inject-bean', version:'2.2.3'])
groovy.grape.Grape.grab([group:'com.jcabi', module:'jcabi-aether', version:'0.10.1'])

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;
import org.apache.maven.artifact.repository.MavenArtifactRepository;
import org.apache.maven.artifact.repository.layout.DefaultRepositoryLayout;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.resolution.DependencyResolutionException;
import org.sonatype.aether.util.artifact.DefaultArtifact;
import org.sonatype.aether.util.artifact.JavaScopes;
import com.jcabi.aether.Aether;
import org.sonatype.aether.repository.RemoteRepository;


MavenProject ret = null;



MavenXpp3Reader mavenReader = new MavenXpp3Reader();
FileReader reader = null;   
     File pomFile = new File("c:/Users/Smith/projects/malabar-mode-jar/pom.xml")
     pomFile.exists();
     reader = new FileReader(pomFile);
              Model model = mavenReader.read(reader);
              model.setPomFile(pomFile);

              ret = new MavenProject(model);
              //ret.getDependencies()
             ret.getDependencies()[0].getVersion()
//new DefaultModelResolver( 
 ret.setRemoteArtifactRepositories(
        Arrays.asList(
            (ArtifactRepository) new MavenArtifactRepository(
                "maven-central", "http://repo1.maven.org/maven2/", new DefaultRepositoryLayout(),
                new ArtifactRepositoryPolicy(), new ArtifactRepositoryPolicy()
            )
        )
    );
    
    Collection<RemoteRepository> remotes = Arrays.asList(
      new RemoteRepository(
        "maven-central",
        "default",
        "http://repo1.maven.org/maven2/"
      )
    );
     String classpath = "";
      Collection<Artifact> deps = new Aether(remotes, new File("c:/Users/Smith/.m2/repository")).resolve(
      new DefaultArtifact("junit", "junit-dep", "", "jar", "4.10"),
      "runtime"
    );
    //Aether aether = new Aether(ret, new File("c:/Users/Smith/.m2/repository"))

             // ret.getArtifactMap()
