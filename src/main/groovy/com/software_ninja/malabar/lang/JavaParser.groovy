
package com.software_ninja.malabar.lang;

import java.util.List;
import java.util.ArrayList;
 
import javax.tools.*;
import javax.tools.JavaCompiler.*;
import javax.tools.StandardLocation;
import java.nio.CharBuffer;

import java.lang.reflect.Modifier;
import groovy.util.logging.*

@Log
public class JavaParser implements Parser {

  def classloader;
  def classesDir

  public JavaParser(classloader, classesDir) {
    this.classloader = classloader;
    this.classesDir = classesDir;
  }

  def parse(File f) { 
    log.fine "FILE:" + f.toURI();
    def fileObjects = new FileJavaFileObject(f);
    parseInternal( [ fileObjects ]);
  }

  def parseInternal(Iterable units) {
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    List rtnval = new ArrayList();
    MyDiagnosticListener listener = new MyDiagnosticListener(rtnval); 
    StandardJavaFileManager fileManager  = compiler.getStandardFileManager(listener, null, null);
    File outputDir = new File(classesDir);
    def localClassloader = new GroovyClassLoader( classloader );
    
    if(! outputDir.exists()) {
      outputDir.mkdirs();
    }
    
    localClassloader.addURL(outputDir.toURL());

    fileManager.setLocation(StandardLocation.CLASS_PATH, classloader.classPath.collect({ new File(it)})); 
    fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Arrays.asList(outputDir));
    def fmClassloader = fileManager.getClassLoader(StandardLocation.CLASS_PATH);

    //String separator = System.getProperty("path.separator");
    //String classpath = classloader.classPath.join(separator)
    def options = ["-g", "-verbose"];

    StringWriter output = new StringWriter();
    JavaCompiler.CompilationTask task = compiler.getTask(output, fileManager, listener, 
							options, null, units);
    Boolean result = task.call();
    //fileManager.list( StandardLocation.CLASS_OUTPUT, "", new HashSet(Arrays.asList(JavaFileObject.Kind.CLASS)), true).each ({println it});

    if(result) {
      def m = output.toString() =~ /\[checking (.*)\]/
      log.info output.toString();
      //log.info classloader.getParent().getClass().toString();
      def clazzes = m.collect({  fmClassloader.loadClass(it[1])});
      def clazz = null;
      if(clazzes.size > 0 ) {
	clazz = clazzes[0];
	println "WHICH:" + fmClassloader.getResource("com/software_ninja/test/project/AppTest.class");
	def publicClasses = clazzes.grep( {  Modifier.isPublic(it.getModifiers())} );
	if(publicClasses.size > 0) {
	  clazz = publicClasses[0];
	} 
      }
      ["class" : clazz,
       "errors" : [] ]

    } else {
      ["class" : null,
       "errors" : listener.getReport()]
    }

  }
  
  def parse(String s) {
    parseInternal( [ new StringJavaFileObject(s) ]);
  }
}

class MyDiagnosticListener implements DiagnosticListener{
  final private List rtnval;

  public MyDiagnosticListener(List rtnval) {
    this.rtnval = rtnval;
  }


  public List getReport() {
    return rtnval;
  }

  public String getSourceLocator(Diagnostic diagnostic) {
    JavaFileObject jfo = diagnostic.getSource();
    return (jfo != null) ? new File(jfo.toUri()) : "";
  }

  public void report(Diagnostic diagnostic) {
    
    rtnval.add( [endColumn     : diagnostic.getColumnNumber(),
		 endLine       : diagnostic.getLineNumber(),
		 line          : diagnostic.getLineNumber(),
		 message       : diagnostic.getMessage(Locale.ENGLISH),
		 sourceLocator : getSourceLocator(diagnostic),
		 startColumn   : diagnostic.getColumnNumber(),
		 column        : diagnostic.getColumnNumber(),
		 startLine     : diagnostic.getLineNumber()]);
    
    // System.out.println("Code->" +  diagnostic.getCode());
    // System.out.println("Column Number->" + diagnostic.getColumnNumber());
    // System.out.println("End Position->" + diagnostic.getEndPosition());
    // System.out.println("Kind->" + diagnostic.getKind());
    // System.out.println("Line Number->" + diagnostic.getLineNumber());
    // System.out.println("Message->"+ diagnostic.getMessage(Locale.ENGLISH));
    // System.out.println("Position->" + diagnostic.getPosition());
    // System.out.println("Source" + diagnostic.getSource());
    // System.out.println("Start Position->" + diagnostic.getStartPosition());
    // System.out.println("\n");
  }
}


