
//groovy.grape.Grape.grab([group:'com.jcabi', module:'jcabi-aether', version:'0.10.1'])
@Grapes([
         @Grab(group='org.apache.maven', module='maven-core', version='3.0.5'),
         @Grab(group='org.apache.maven', module='maven-compat', version='3.0.5'),
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

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;

import org.apache.maven.model.building.DefaultModelBuildingRequest

import org.apache.maven.repository.legacy.LegacyRepositorySystem;
import org.apache.maven.repository.RepositorySystem;


import org.apache.maven.artifact.repository.layout.DefaultRepositoryLayout

import org.apache.maven.repository.internal.MavenServiceLocator

import org.apache.maven.project.ProjectBuildingRequest
import org.apache.maven.project.DefaultProjectBuildingRequest
import org.sonatype.aether.util.DefaultRepositorySystemSession
import org.apache.maven.artifact.repository.LegacyLocalRepositoryManager
import org.apache.maven.repository.RepositorySystem;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy
import org.apache.maven.artifact.repository.MavenArtifactRepository
    import org.apache.maven.repository.internal.MavenRepositorySystemSession;
import org.sonatype.aether.impl.internal.SimpleLocalRepositoryManager;
import org.apache.maven.project.DefaultProjectBuilder

File local = new File("~/.m2/repository");
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

MavenXpp3Reader mavenReader = new MavenXpp3Reader();
FileReader reader = null;   
File pomFile = new File("c:/Users/lpmsmith/projects/malabar-mode-jar/pom.xml");
reader = new FileReader(pomFile);
Model model = mavenReader.read(reader);
model.setPomFile(pomFile);

MavenProject proj = new MavenProject(model);
println proj
    println proj.getDependencies();
    println proj.getProjectBuildingRequest();



 DefaultModelBuildingRequest mbr = new DefaultModelBuildingRequest();
mbr.setPomFile(pomFile);



// org.apache.maven.project.LegacyLocalRepositoryManager






    getProject(pomFile);

def lookup( Class clazz)
{
    MavenServiceLocator  msr = new MavenServiceLocator();
    RepositorySystem drs = new LegacyRepositorySystem()
        //drs.initService(msr)

    return msr.getService(clazz);
}




// http://maven.apache.org/ref/3.2.2/maven-core/xref-test/org/apache/maven/project/AbstractMavenProjectTestCase.html

def MavenProject getProject( File pom )
         throws Exception
     {
         ProjectBuildingRequest configuration = newBuildingRequest();
 
         return new DefaultProjectBuilder().build( pom, configuration ).getProject();
     }
 
def ProjectBuildingRequest newBuildingRequest()
     {
         ProjectBuildingRequest configuration = new DefaultProjectBuildingRequest();

         configuration.setLocalRepository( getLocalRepository() );
         initRepoSession( configuration );
         return configuration;
     }
 

def initRepoSession( ProjectBuildingRequest request ) {
         File localRepo = new File( request.getLocalRepository().getBasedir() );
         MavenRepositorySystemSession repoSession = new MavenRepositorySystemSession();
         repoSession.setLocalRepositoryManager( new SimpleLocalRepositoryManager( localRepo ) );
         request.setRepositorySession( repoSession );
     }
def ArtifactRepository getLocalRepository()
         throws Exception
     {

         ArtifactRepositoryLayout repoLayout = new DefaultRepositoryLayout()
 
             ArtifactRepository r = new MavenArtifactRepository( "local", "file://c:/Users/lpmsmith/.m2/repository" , repoLayout, new ArtifactRepositoryPolicy(), new ArtifactRepositoryPolicy() );
 
         return r;
     }

