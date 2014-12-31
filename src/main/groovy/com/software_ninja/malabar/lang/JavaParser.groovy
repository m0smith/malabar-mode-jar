
package com.software_ninja.malabar.lang;
import java.util.List;
import java.util.ArrayList;

import javax.tools.*;
import javax.tools.JavaCompiler.*;
 
public class JavaParser implements Parser {

  def classloader;

  public JavaParser(classloader) {
    this.classloader = classloader;
  }


  def parse(File f) {
    List rtnval = new ArrayList();
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    MyDiagnosticListener listener = new MyDiagnosticListener(rtnval); 
    StandardJavaFileManager fileManager  = compiler.getStandardFileManager(listener, null, null);

    Iterable fileObjects = fileManager.getJavaFileObjectsFromStrings(Arrays.asList(f.getAbsolutePath()));
    String separator = System.getProperty("path.separator");
    String classpath = classloader.classPath.join(separator)
    def options = ["-classpath",  classpath];

    JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, listener, 
							options, null, fileObjects);
    Boolean result = task.call();

    return result ? ["class" : String.class,
		     "errors" : [] ] : 
		     ["class" : null,
		      "errors" : listener.getReport()];

  }
  
  Class<?> parse(String s) {
    return null;
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

  public void report(Diagnostic diagnostic) {
    
    rtnval.add( [endColumn : diagnostic.getColumnNumber(),
		 endLine : diagnostic.getLineNumber(),
		 line : diagnostic.getLineNumber(),
		 message : diagnostic.getMessage(Locale.ENGLISH),
		 sourceLocator : "",
		 startColumn : diagnostic.getColumnNumber(),
		 column : diagnostic.getColumnNumber(),
		 startLine : diagnostic.getLineNumber()]);
    
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
