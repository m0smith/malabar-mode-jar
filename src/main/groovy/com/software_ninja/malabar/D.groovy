@Grapes([
         @Grab(group='org.apache.maven', module='maven-core', version='3.0.5'),
         @Grab(group='org.apache.maven', module='maven-compat', version='3.0.5'),
         @Grab(group='com.jcabi', module='jcabi-aether', version='0.10.1')
         ])

         // http://www.programcreek.com/java-api-examples/index.php?api=org.apache.maven.project.MavenProjectBuilder See # 20

import org.codehaus.plexus.DefaultPlexusContainer
import org.apache.maven.project.MavenProjectBuilder
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.project.DefaultProjectBuilderConfiguration
import org.apache.maven.artifact.repository.DefaultArtifactRepository
import com.jcabi.aether.Aether
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.util.artifact.DefaultArtifact;
import org.sonatype.aether.artifact.Artifact;


container=new DefaultPlexusContainer();
projectBuilder=(MavenProjectBuilder)container.lookup(MavenProjectBuilder.class.getName());
layout=(ArtifactRepositoryLayout)container.lookup(ArtifactRepositoryLayout.class.getName(),"default");

def projectInfo(localRepoUrl, pom){

    File pomFile = new File(pom);
    String localRepoUrl2 = "file://" + localRepoUrl;
    File local = new File(localRepoUrl);



    ArtifactRepository localRepo=new DefaultArtifactRepository("local",localRepoUrl2,layout);
    pbConfig=new DefaultProjectBuilderConfiguration().setLocalRepository(localRepo);
    project = projectBuilder.build( pomFile, pbConfig );
    aether = new Aether(project, local);
    [ runtime: resolveDependencies(aether, project, "runtime"),
      test : resolveDependencies(aether, project, "test") ];
}


def resolveDependencies (aether, project, scope) {
    depLists = project.getDependencies().collect { 
    
        art = new DefaultArtifact(it.getGroupId(), it.getArtifactId(), it.getClassifier(), it.getType(), 
                                  it.getVersion());
        Collection<Artifact> deps = aether.resolve( art, scope );

        deps.collect{  it.getFile().getAbsolutePath() }
        
    }

    [ dependencies : depLists.collect {it.first()},  classpath : depLists.flatten() ]
}



println projectInfo("c:/Users/lpmsmith/.m2/repository", "pom.xml");



