// To RUN
// groovy -Dhttp.proxyHost=proxy.ihc.com -Dhttp.proxyPort=8080 -Dgroovy.grape.report.downloads=true -Djava.net.useSystemProxies=true src/main/groovy/com/software_ninja/malabar/D.groovy
// https://docs.oracle.com/javase/7/docs/api/java/net/doc-files/net-properties.html

//  groovy.grape.Grape.grab([group:'org.apache.maven', module:'maven-core', version:'3.0.5']);
//  groovy.grape.Grape.grab([group:'org.apache.maven', module:'maven-compat', version:'3.0.5']);
//  groovy.grape.Grape.grab([group:'com.jcabi', module:'jcabi-aether', version:'0.10.1']);


         // http://www.programcreek.com/java-api-examples/index.php?api=org.apache.maven.project.MavenProjectBuilder See # 20

package com.software_ninja.malabar.project;

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

  
  def projectInfo2(String localRepoUrl, String pom){
    return "localRepoUrl:" + localRepoUrl + " pom:" + pom;
  }

  def projectInfo(String localRepoUrl, String pom){
    container=new DefaultPlexusContainer();
    println("localRepoUrl:" + localRepoUrl + " pom:" + pom)
    File pomFile = new File(pom);
    String localRepoUrl2 = "file://" + localRepoUrl;
    File local = new File(localRepoUrl);

   println container.getClass().getName();
   projectBuilder=(MavenProjectBuilder)container.lookup(MavenProjectBuilder.class.getName());
   println projectBuilder.getClass().getName();
   layout=(ArtifactRepositoryLayout)container.lookup(ArtifactRepositoryLayout.class.getName(),"default");
   println layout.getClass().getName();


    ArtifactRepository localRepo=new DefaultArtifactRepository("local",localRepoUrl2,layout);
       println localRepo.getClass().getName();

    pbConfig=new DefaultProjectBuilderConfiguration().setLocalRepository(localRepo);
       println pbConfig.getClass().getName();

    project = projectBuilder.build( pomFile, pbConfig );
       println project.getClass().getName();

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


//  this.getClass().classLoader.rootLoader.addURL(new File("c:/Users/lpmsmith/projects/malabar-mode-jar/build/libs/malabar-mode-jar.jar").toURL())
//  new D().projectInfo("c:/Users/lpmsmith/.m2/repository", "c:/Users/lpmsmith/projects/malabar-mode-jar/pom.xml");
//  new com.software_ninja.malabar.D().projectInfo("c:/Users/lpmsmith/.m2/repository", "c:/Users/lpmsmith/projects/malabar-mode-jar/pom.xml");
//  new com.software_ninja.malabar.project.MavenProject().projectInfo("c:/Users/lpmsmith/.m2/repository", "c:/Users/lpmsmith/projects/malabar-mode-jar/pom.xml");

// 1 + 2

// this.getClass().classLoader.rootLoader.addURL(new File("c:/Users/lpmsmith/projects/malabar-mode-jar/target/classes").toURL())
// new D().projectInfo("c:/Users/lpmsmith/.m2/repository", "c:/Users/lpmsmith/projects/malabar-mode-jar/pom.xml");
// projectInfo("c:/Users/lpmsmith/.m2/repository", "pom.xml");


//:load  file:/c:/Users/lpmsmith/projects/malabar-mode-jar/src/main/groovy/com/software_ninja/malabar/D.groovy


