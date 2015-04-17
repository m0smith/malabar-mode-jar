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



import com.software_ninja.malabar.MalabarUtil 
import com.software_ninja.malabar.ResourceCache;
import com.software_ninja.malabar.SemanticReflector;
import com.software_ninja.malabar.lang.GroovyParser;
import com.software_ninja.malabar.lang.JavaParser;

import org.codehaus.groovy.control.ErrorCollector;
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ASTTransformationCustomizer

import org.junit.runner.Result;
import org.junit.runner.Request;
import org.junit.runner.JUnitCore;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import groovy.util.logging.*

@Log
public class MavenProjectHandler {

  /**
   *  cache path [this.getClass().getName()]
   *             [pom]
   *  cache keys [timestamp projectInfo cloassLoader]
   */
  public Map cache;

  public List relative;
  public List absolute;
  
  public MavenProjectHandler(config) {
    this.cache = config['cache'];
  }

  /**
   * with static compiler
   */
  def createClassLoaderStatic(paths) {
    def config = new CompilerConfiguration();
    def acz = new ASTTransformationCustomizer( groovy.transform.CompileStatic );
    config.addCompilationCustomizers(acz);
    
    def rtnval = new GroovyClassLoader(Thread.currentThread().getContextClassLoader() ,config);
    paths.each( { path ->  if(path != null) rtnval.addURL(new File(path as String).toURL()); });
    return rtnval;
  }

  /**
   * without static compiler
   */
  def createClassLoader(paths) {
    def rtnval = new GroovyClassLoader(Thread.currentThread().getContextClassLoader());
    paths.each( { path ->  if( path != null ) rtnval.addURL(new File(path as String).toURL()); });
    return rtnval;
  }

  def createCacheEntry(pomFile, mod, func) {
    def projectInfo = func();
    def parent = pomFile.getParent();
    def relPaths = relative.collect({new File (parent, it).getAbsolutePath()});
    def bootClasspath = System.getProperty("sun.boot.class.path");
    log.fine "RELPATHS" + relPaths;
    def classpath = relPaths + 
    absolute + 
    projectInfo['test']['resources'].collect({it.directory}) +
      projectInfo['runtime']['resources'].collect({it.directory}) +
      projectInfo['test']['sources'] +
      projectInfo['runtime']['sources'] +
      projectInfo['test']['elements'] +
      projectInfo['runtime']['elements'] +
      projectInfo['test']['classpath'];
      def resourceCache = new ResourceCache();
      classpath.each({if(it != null) resourceCache.submit(it)});
      bootClasspath.split(System.getProperty("path.separator")).each({if(it != null) resourceCache.submit(it)});

      def staticClassloader = createClassLoaderStatic(classpath);
      def classloader = createClassLoader(classpath);
      //log.fine classpath
      def rtnval = [timestamp : mod,
		    projectInfo : projectInfo,
		    resourceCache : resourceCache,
		    parsers : [ "groovy-strict" : new GroovyParser(staticClassloader),
				groovy          : new GroovyParser(classloader),
				java            : new JavaParser(classloader, projectInfo['test']['elements'][0])],
		    classLoader : classloader,
		    classLoaderStatic : staticClassloader];
      return rtnval;
  }
  
  def clearCache() {
    def name = this.getClass().getName();
    def f = cache[name];
    if( f != null )   f.clear();
  }

  def lookInCache(pm, pmfile, func) {
    def name = this.getClass().getName();
    def pomFile = new File(MalabarUtil.expandFile(pmfile as String));
    def mod = pomFile.lastModified();
    def cache1 = cache[name];
    if(cache1 == null) {
      cache1 = [:]
      cache.put(name, cache1);
    }
    def key = [pm, pmfile];
    def rtnval = cache1[key];
  
    if(rtnval == null || rtnval['timestamp'] != mod) {
      log.fine("cache miss:" + pm + " " + pmfile);
      rtnval = createCacheEntry(pomFile, mod , func); 
      cache[name].put( key , rtnval);
    }
    return rtnval;
  }

  //
  // Add extra class path elements
  //

  def additionalClasspath(relativeJson ,absoluteJson) {
    
    def newRelative = relativeJson != null ? new groovy.json.JsonSlurper().parseText (relativeJson ) : [];
    def newAbsolute = absoluteJson != null ? new groovy.json.JsonSlurper().parseText (absoluteJson ) : [];

    if( relative != newRelative || absolute != newAbsolute) {
      this.relative = newRelative;
      this.absolute = newAbsolute;
      clearCache();
    }
    
  }
  
  //
  // Parsing
  //


  def handleUnitTestCompileException(org.codehaus.groovy.control.messages.SyntaxErrorMessage ex) {
    def cause = ex.getCause();
    return [ "Compile Error (" + cause.line +',' + cause.startColumn +")", 
	     cause.message, 
	     '',
	     ''];

  }

  def handleUnitTestCompileException(org.codehaus.groovy.control.messages.ExceptionMessage ex) {
    def regex = /.*At \[(\d+):(\d+)\] (.*)/
    def message = ex.cause.message;
    def matcher = ( message =~ regex );
    log.fine matcher.matches().toString()
    log.fine matcher[0].toString()
    if (matcher.groupCount() > 0) {
      def line = matcher[0][1];
      def col =  matcher[0][2];
      def source =  matcher[0][3];
      return [ "Compile Error (" + line +',' + col +")", 
	       message, 
	       '',
	       ex.getStackTrace()]

    }
    return [ "Compile Error",
	       message, 
	       '',
	       ex.getStackTrace()];
  }
  
  def fixArgs(args) {
    if(args == null) return new String[0];
    if(args instanceof String) { def r = new String[1]; r[0] = args; return r;};
    return args as String[];
  }


  
  /**
   * Load and execute the class.  Assumes the class has been compiled/parsed already.
   */
  def exec(repo, pm, pmfile, clazzName, args) { 
    log.fine "Start Exec of " + clazzName;

    try{

      def cached = lookInCache( pm, pmfile, { fecthProjectInfo(repo, pm, pmfile)});

      def cl = cached['classLoader'];
      def clazz = Class.forName(clazzName, true, cl);
      clazz.main(fixArgs(args));

    } catch (Exception ex){
      ex.printStackTrace();
    }
  }

  /**
   * Parse the script on disk.  Return errors as a list
   */
  def parse(repo, pm, pmfile, scriptIn, scriptBody, parserName) {
    log.fine "Start Parse";
    try{

      def cached = lookInCache( pm, pmfile, { fecthProjectInfo(repo, pm, pmfile)});

      def parser = cached['parsers'][parserName];
      def rtnval = null;
      if(scriptBody == null) {
	def script = MalabarUtil.expandFile(scriptIn);
	log.fine "PArsing script:" + script + " with parser:" + parser;
	rtnval = parser.parse(new File(script));

      }  else {
	log.fine "PArsing scriptBody: with parser:" + parser;
	rtnval = parser.parse(scriptBody);

      }
      log.fine "parsed fine";
      rtnval['class'] == null ? rtnval['errors'] : [];

    } catch (Exception ex){
      ex.printStackTrace();
    }
    
    
  }

  /**
   * Run a unit test.  Return a list of failures.
   */

  def unitTest (repo, pm, pmfile, scriptIn, method, parserName) {    
    String script = MalabarUtil.expandFile(scriptIn);
    def cached = lookInCache( pm, pmfile, { fecthProjectInfo(repo, pm, pmfile)});
    try{
      def parser = cached['parsers'][parserName];
      def parseResult = parser.parse(new File(script));
      def clazz = parseResult['class'];
      if(clazz == null) {
	return parseResult['errors'].collect( { [it['header'],
						 it['message'],
						 it['exceptionMessage'],
						 it['stackTrace']]});
      }
      Request request = Request.method(clazz,method);
      log.fine "UnitTest "+ clazz.getName() + " ..."
      
      if( method == null ) {
	request = Request.aClass(clazz);
      } else {
	request = Request.method(clazz,method);
      }

      Result result = new JUnitCore().run(request);
      log.fine "UnitTest ... Complete:" + result.getFailureCount()
      return result.getFailures().collect( { [ it.getTestHeader(),
					       it.getMessage(),
					       it.getException().getMessage(),
					       it.getTrace()]} );
    } catch (Exception ex) {
      ex.printStackTrace();
      [[ "Compile Error",
	ex.getMessage(), 
	'',
	ex.getStackTrace()]]

    }
    

  }


  /**
   * Get info on a class or resource
   *
   *   @param isClass If null or true, look for only class names
   *   @param useRegex If null or true, treat pattern as a regex
   */
  def resource(repo, pm, pmfile, pattern, max, isClass, useRegex){
    def cached = lookInCache( pm, pmfile, { fecthProjectInfo(repo, pm, pmfile)});
    def resourceCache = cached['resourceCache'];
    log.fine "RESOURCE:" + resourceCache + " " + isClass + " " + useRegex + " " + max;
    if( isClass == null || isClass ){

      resourceCache.findClass(pattern, max);
    } else if(useRegex == null || useRegex) {
      resourceCache.find(pattern, max);
    } else {
      resourceCache.findExact(pattern, max);
    }
  } 


  /**
   * Get info on a class or resource
   *
   *   @param isClass If null or true, look for only class names
   *   @param useRegex If null or true, treat pattern as a regex
   */
  def tags(repo, pm, pmfile, className){
    def cached = lookInCache( pm, pmfile, { fecthProjectInfo(repo, pm, pmfile)});
    def classLoader = cached.get('classLoader');
    new SemanticReflector().asSemanticTag(classLoader.loadClass(className));

  } 


  /**
   * Return current status of a project.
   *
   */

  def debug(repo, pm, pmfile){
    def cached = lookInCache( pm, pmfile, { fecthProjectInfo(repo, pm, pmfile)});
    def classLoader = cached.get('classLoader');
    [ classpath   : classLoader.classPath,
      projectInfo : cached['projectInfo'],
      timestamp   : cached['mod'] ];

  } 




  //
  // Project Info
  //

  def fecthProjectInfo = { repo, pm, pmfile -> 
    def repox = (repo == null ? "~/.m2/repository" : repo);

    try {
      def x = pm == "gradle" ? new GradleProjectsCreator() : new MavenProjectsCreator();
      def pjs = x.create(MalabarUtil.expandFile(repox), MalabarUtil.expandFile(pmfile))
      return [runtime: x.resolveDependencies(pjs[0], repox, "runtime"),
	      systemProperties : System.getProperties(), 
	      test:    x.resolveDependencies(pjs[0], repox, "test")]



    } catch (Exception ex) {
      ex.printStackTrace();
      throw new Exception( ex.getMessage() + " repo:" + 
			   MalabarUtil.expandFile(repox) + 
			   " pmfile:" + MalabarUtil.expandFile(pmfile), ex);
    }
  }
  
  def projectInfo(repo, pm, pmfile) {
    return lookInCache(pm, pmfile, { fecthProjectInfo(repo, pm, pmfile)} )['projectInfo'];
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
