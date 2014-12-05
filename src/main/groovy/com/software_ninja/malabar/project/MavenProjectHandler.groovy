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

import org.sonatype.aether.graph.DependencyNode

import com.software_ninja.malabar.MalabarUtil

import org.codehaus.groovy.control.ErrorCollector;
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ASTTransformationCustomizer

import java.io.File;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;


public class MavenProjectHandler {

  /**
   *  cache path [this.getClass().getName()]
   *             [pom]
   *  cache keys [timestamp projectInfo cloassLoader]
   */
  public Map cache;
  
  public MavenProjectHandler(config) {
    this.cache = config['cache'];
  }

  /**
   * with static compiler
   */
  def createClassLoader(paths) {
    def config = new CompilerConfiguration();
    def acz = new ASTTransformationCustomizer( groovy.transform.CompileStatic );
    config.addCompilationCustomizers(acz);
    
    def rtnval = new GroovyClassLoader(Thread.currentThread().getContextClassLoader() ,config);
    paths.each( { path ->  rtnval.addURL(new File(path).toURL()); });
    return rtnval;
  }

  def createCacheEntry(mod, func) {
    def projectInfo = func();
    def classpath = projectInfo['test']['classpath'];
    println classpath
    def rtnval = [timestamp : mod,
	      projectInfo : projectInfo,
	      classLoader : createClassLoader(classpath)];
    return rtnval;
  }
  
  def lookInCache(pom, func) {
    def name = this.getClass().getName();
    def pomFile = new File(pom);
    def mod = pomFile.lastModified();
    def cache1 = cache[name];
    if(cache1 == null) {
      cache1 = [:]
      cache.put(name, cache1);
    }
    
    def rtnval = cache1[pom];
  
    if(rtnval == null || rtnval['timestamp'] != mod) {
      rtnval = createCacheEntry(mod , func); 
      cache[name].put( pom , rtnval);
    }
    return rtnval;
  }

  //
  // Parsing
  //

  def handleException(org.codehaus.groovy.control.messages.SyntaxErrorMessage ex) {
    def cause = ex.getCause();
    return [endColumn : cause.endColumn,
	    endLine : cause.endLine,
	    line : cause.line,
	    message : cause.message,
	    sourceLocator : cause.sourceLocator,
	    startColumn : cause.startColumn,
	    startLine : cause.startLine];
  }

  def handleException(org.codehaus.groovy.control.messages.ExceptionMessage ex) {
    def regex = /.*At \[(\d+):(\d+)\] (.*)/
    def message = ex.cause.message;
    def matcher = ( message =~ regex );
    println matcher.matches()
    println matcher[0]
    if (matcher.groupCount() > 0) {
      def line = matcher[0][1];
      def col =  matcher[0][2];
      def source =  matcher[0][3];
      return [endColumn : (col as int) + 1 + "",
	      endLine : line,
	      line : line,
	      message : message,
	      sourceLocator : source,
	      startColumn : col,
	      startLine : line];
    }
    return [message : message];
  }
  
  /**
   * Parse the script on disk.  Return errors as a list
   */
  def parse(repo, pom, scriptIn) {
    def script = MalabarUtil.expandFile(scriptIn);
    def cached = lookInCache( pom, { fecthProjectInfo(repo, pom)});
    try{
      def classLoader = cached.get('classLoader');
      classLoader.clearCache();
      classLoader.parseClass(new File(script));
      println "parsed fine";
      return [];
    } catch (org.codehaus.groovy.control.MultipleCompilationErrorsException ex){
      println ex
      def rtnval = [];
      ErrorCollector collector = ex.getErrorCollector();
      collector.getErrors().collect( { handleException(it) });
    }
    
    
  }

  //
  // Project Info
  //

  def fecthProjectInfo = { repo, pom -> 
    try {
      def x = new MavenProjectsCreator();
      def repox = (repo == null ? "~/.m2/repository" : repo);
      def pjs = x.create(MalabarUtil.expandFile(repox), MalabarUtil.expandFile(pom))
      return [runtime: x.resolveDependencies(pjs[0], repox, "runtime"),
	      test:    x.resolveDependencies(pjs[0], repox, "test")]
    } catch (Exception ex) {
      throw new Exception( ex.getMessage() + " repo:" + repox + " pom:" + pom, 
			   ex);
    }
  }
  
  def projectInfo(repo, pom) {
    return lookInCache(pom, { fecthProjectInfo(repo, pom)} )['projectInfo'];
  }
		       
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
	  //println "" + rtnval + " NODE:" + optional + ' ' + artifactId + " " + parents;
	  
	  return rtnval;
	  
	}
      }
      
      Collection<Artifact> deps = aether.resolve( art, scope , filter);
      
      deps.collect{  it.getFile().getAbsolutePath() }
      
    }.grep({it.size() > 0})
    
    [ dependencies : depLists.collect {it.first()},  classpath : depLists.flatten() ,
      resources: "test"== scope ? project.getTestResources() : project.getResources() ,
      sources : "test"== scope ? project.getTestCompileSourceRoots() :project.getCompileSourceRoots() ,
      elements:  "test"== scope ? project.getTestClasspathElements() : project.getCompileClasspathElements()]
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


