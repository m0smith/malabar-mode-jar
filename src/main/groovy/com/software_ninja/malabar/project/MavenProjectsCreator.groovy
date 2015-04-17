package com.software_ninja.malabar.project;

import org.apache.maven.execution.MavenSession;

import org.apache.maven.execution.MavenExecutionResult;

import org.apache.maven.execution.DefaultMavenExecutionResult;

import org.apache.maven.execution.DefaultMavenExecutionRequest;

import org.apache.maven.project.ProjectBuildingRequest;

import org.apache.maven.project.ProjectBuildingResult;

import org.apache.maven.execution.MavenExecutionRequestPopulator;

import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.project.ProjectBuilder;


import java.util.logging.Level;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.execution.MavenExecutionRequestPopulationException;

import groovy.util.logging.Log;
import java.util.List;
import org.apache.maven.project.MavenProject;
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
import com.jcabi.aether.Aether
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.util.artifact.DefaultArtifact;
import org.sonatype.aether.artifact.Artifact;

import org.sonatype.aether.graph.DependencyNode

@Log
public class MavenProjectsCreator {
  public List<MavenProject> create(String repo, String pom) {
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

  private List<MavenProject> createNow(Settings settings, File pomFile) throws PlexusContainerException, PlexusConfigurationException, ComponentLookupException, MavenExecutionRequestPopulationException, ProjectBuildingException {
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
    List<MavenProject> reactorProjects = new ArrayList<MavenProject>();
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


  public Map resolveDependencies(MavenProject project, repo, scope) {

    File local = new File(repo);
    Aether.class.getClassLoader().findResources("com/jcabi/aether/Aether.class").each({log.fine it.toString() });
    Aether aether = new Aether(project, local);
    List depLists = project.getDependencies().collect { 
      
      DefaultArtifact  art = new DefaultArtifact(it.getGroupId(), it.getArtifactId(), 
					       it.getClassifier(), it.getType(),it.getVersion());

      org.sonatype.aether.graph.DependencyFilter filter = new org.sonatype.aether.graph.DependencyFilter() {
	boolean accept( DependencyNode node, List<DependencyNode> parents )
	{
	  String artifactId = node.getDependency().getArtifact().getArtifactId();
	  boolean optional = node.getDependency().isOptional();
	  boolean rtnval =  ! optional && ! (['activation', 'xerces-impl', 'ant', 
					      'com.springsource.org.hibernate.validator-4.1.0.GA',
					      'xerces-impl-2.6.2'].contains(artifactId));
	  log.fine "" + rtnval + " NODE:" + optional + ' ' + artifactId + " " + parents;
	  
	  return rtnval;
	  
	}
      }
      try {




	Collection<Artifact> deps = aether.resolve( art, scope , filter);
	
	deps.collect{  it.getFile().getAbsolutePath() }
      } catch (  org.sonatype.aether.resolution.DependencyResolutionException ex) {
	log.log(Level.WARNING, ex.getResult().toString(), ex);
	[];
      } catch (Exception ex) {
	log.log(Level.WARNING, ex.getMessage(), ex);
	[];
    }
      
    }.grep({it.size() > 0})
    
    [ dependencies : depLists.collect {it.first()},  classpath : depLists.flatten() ,
      resources: "test"== scope ? project.getTestResources() : project.getResources() ,
      sources : "test"== scope ? project.getTestCompileSourceRoots() :project.getCompileSourceRoots() ,
      elements:  "test"== scope ? project.getTestClasspathElements() : project.getCompileClasspathElements()]
  }

}

