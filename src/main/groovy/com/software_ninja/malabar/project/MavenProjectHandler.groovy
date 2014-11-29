// To RUN
// groovy -Dhttp.proxyHost=proxy.ihc.com -Dhttp.proxyPort=8080 -Dgroovy.grape.report.downloads=true -Djava.net.useSystemProxies=true src/main/groovy/com/software_ninja/malabar/D.groovy
// https://docs.oracle.com/javase/7/docs/api/java/net/doc-files/net-properties.html

//groovy.grape.Grape.grab([group:'org.apache.maven', module:'maven-core', version:'3.0.5']);
//groovy.grape.Grape.grab([group:'org.apache.maven', module:'maven-compat', version:'3.0.5']);
//groovy.grape.Grape.grab([group:'com.jcabi', module:'jcabi-aether', version:'0.10.1']);


// http://www.programcreek.com/java-api-examples/index.php?api=org.apache.maven.project.MavenProjectBuilder See # 20
// See also https://code-review.gradle.org/changelog/Gradle?cs=5272714e45bf6eb2cd87c1c7611fde5ddd32cdc5
// https://github.com/gradle/gradle/blob/master/subprojects/build-init/src/main/groovy/org/gradle/buildinit/plugins/internal/maven/MavenProjectsCreator.java

package com.software_ninja.malabar.project;



/*
 * Stolen wholesale from https://github.com/gradle/gradle/blob/master/subprojects/build-init/src/main/groovy/org/gradle/buildinit/plugins/internal/maven/MavenProjectsCreator.java
 */

//this.getClass().classLoader.rootLoader.addURL(new File("c:/Users/lpmsmith/projects/malabar-mode-jar/build/libs/malabar-mode-jar.jar").toURL())


//import com.google.common.collect.ImmutableList;
//import org.gradle.api.Transformer;
//import org.gradle.internal.SystemProperties;
import org.apache.maven.execution.*;
import org.apache.maven.project.*;
import org.apache.maven.settings.Settings;
import org.codehaus.plexus.ContainerConfiguration;
import org.codehaus.plexus.DefaultContainerConfiguration;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.util.DefaultRepositorySystemSession;
//import org.gradle.util.CollectionUtils;
import com.jcabi.aether.Aether
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.util.artifact.DefaultArtifact;
import org.sonatype.aether.artifact.Artifact;


import java.io.File;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;



def projectInfo(repo, pom) {
  x = new MavenProjectsCreator();
  pjs = x.create(repo, pom)
  return [runtime: x.resolveDependencies(pjs[0], repo, "runtime"),
	  test:   x.resolveDependencies(pjs[0], repo, "test")]
}


public class MavenProjectsCreator {
  public Set<MavenProject> create(String repo, String pom) {
    Settings mavenSettings = new Settings();
    mavenSettings.setLocalRepository(repo);
    File pomFile = new File(pom);
    if (!pomFile.exists()) {
      throw new Exception(String.format("Unable to create Maven project model. The POM file %s does not exist.", pomFile));
    }
    try {
      return createNow(mavenSettings, pomFile);
    } catch (Exception e) {
      e.printStackTrace();
      throw new Exception(String.format("Unable to create Maven project model using POM %s.", pomFile), e);
    }
  }
  private Set<MavenProject> createNow(Settings settings, File pomFile) throws PlexusContainerException, PlexusConfigurationException, ComponentLookupException, MavenExecutionRequestPopulationException, ProjectBuildingException {
    //using jarjar for maven3 classes affects the contents of the effective pom
    //references to certain Maven standard plugins contain jarjar in the fqn
    //not sure if this is a problem.
    ContainerConfiguration containerConfiguration = new DefaultContainerConfiguration()
    .setClassWorld(new ClassWorld("plexus.core", ClassWorld.class.getClassLoader()))
    .setName("mavenCore");
    DefaultPlexusContainer container = new DefaultPlexusContainer(containerConfiguration);
    ProjectBuilder builder = container.lookup(ProjectBuilder.class);
    MavenExecutionRequest executionRequest = new DefaultMavenExecutionRequest();
    final Properties properties = new Properties(System.getProperties());
    //    properties.putAll(SystemProperties.asMap());
    executionRequest.setSystemProperties(properties);
    MavenExecutionRequestPopulator populator = container.lookup(MavenExecutionRequestPopulator.class);
    populator.populateFromSettings(executionRequest, settings);
    populator.populateDefaults(executionRequest);
    ProjectBuildingRequest buildingRequest = executionRequest.getProjectBuildingRequest();
    buildingRequest.setProcessPlugins(false);
    MavenProject mavenProject = builder.build(pomFile, buildingRequest).getProject();
    Set<MavenProject> reactorProjects = new LinkedHashSet<MavenProject>();
    //TODO adding the parent project first because the converter needs it this way ATM. This is oversimplified.
    //the converter should not depend on the order of reactor projects.
    //we should add coverage for nested multi-project builds with multiple parents.
    reactorProjects.add(mavenProject);
    List<ProjectBuildingResult> allProjects = builder.build([pomFile], true, buildingRequest);

    allProjects.collect( { p -> reactorProjects.add(p.getProject()); })
    
    // CollectionUtils.collect(allProjects, reactorProjects, new Transformer<MavenProject, ProjectBuildingResult>() {
    // 			      public MavenProject transform(ProjectBuildingResult original) {
    // 				return original.getProject();
    // 			      }
    // 			    });


    MavenExecutionResult result = new DefaultMavenExecutionResult();
    result.setProject(mavenProject);
    RepositorySystemSession repoSession = new DefaultRepositorySystemSession();
    MavenSession session = new MavenSession(container, repoSession, executionRequest, result);
    session.setCurrentProject(mavenProject);
    return reactorProjects;
  }


  public Map resolveDependencies(project, repo, scope) {

    File local = new File(repo);
    Aether aether = new Aether(project, local);
    List depLists = project.getDependencies().collect { 
      
    DefaultArtifact  art = new DefaultArtifact(it.getGroupId(), it.getArtifactId(), it.getClassifier(), it.getType(), 
				it.getVersion());
      
      Collection<Artifact> deps = aether.resolve( art, scope );
      
      deps.collect{  it.getFile().getAbsolutePath() }
      
    }
    
    [ dependencies : depLists.collect {it.first()},  classpath : depLists.flatten() ]
  }

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


