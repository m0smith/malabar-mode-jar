@Grapes([
         @Grab(group='org.apache.maven', module='maven-core', version='3.0.5'),
         @Grab(group='org.apache.maven', module='maven-compat', version='3.0.5'),
         @Grab(group='com.jcabi', module='jcabi-aether', version='0.10.1')
         ])

         // http://www.programcreek.com/java-api-examples/index.php?api=org.apache.maven.project.MavenProjectBuilder See # 20

import org.codehaus.plexus.DefaultPlexusContainer
import  org.apache.maven.project.MavenProjectBuilder
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.artifact.repository.ArtifactRepository;
import  org.apache.maven.project.DefaultProjectBuilderConfiguration
import org.apache.maven.artifact.repository.DefaultArtifactRepository

File pomFile = new File("c:/Users/lpmsmith/projects/malabar-mode-jar/pom.xml");
String localRepoUrl = "file://c:/Users/lpmsmith/.m2/repository";

container=new DefaultPlexusContainer();

projectBuilder=(MavenProjectBuilder)container.lookup(MavenProjectBuilder.class.getName());
ArtifactRepositoryLayout layout=(ArtifactRepositoryLayout)container.lookup(ArtifactRepositoryLayout.class.getName(),"default");
ArtifactRepository localRepo=new DefaultArtifactRepository("local",localRepoUrl,layout);
pbConfig=new DefaultProjectBuilderConfiguration().setLocalRepository(localRepo);

project = projectBuilder.build( pomFile, pbConfig );

println project.getDependencies()
