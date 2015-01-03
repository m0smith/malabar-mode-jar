package com.software_ninja.malabar.lang;

import org.codehaus.groovy.control.ErrorCollector;
import groovy.util.logging.*

public class GroovyParser implements Parser {

  def classloader;

  def handleException(org.codehaus.groovy.control.messages.SyntaxErrorMessage ex) {
    def cause = ex.getCause();
    return [endColumn : cause.endColumn,
	    endLine : cause.hasProperty('endLine')? cause.endLine :cause.line,
	    line : cause.line,
	    message : cause.message,
	    exceptionMessage : cause.message,
	    header: "Compile Error (" + cause.line +',' + cause.startColumn +")",
	    stackTrace : cause != null ? cause.getStackTrace() : ex.getStackTrace(),
	    sourceLocator : cause.sourceLocator,
	    startColumn : cause.startColumn,
	    column : cause.startColumn,
	    startLine : cause.hasProperty('startLine')? cause.startLine :cause.line];
  }

  def handleException(org.codehaus.groovy.control.messages.ExceptionMessage ex) {
    def regex = /.*At \[(\d+):(\d+)\] (.*)/
    def message = ex.cause.message;
    def matcher = ( message =~ regex );
    log.fine matcher.matches().toString()
    log.fine matcher[0].toString()
    if (matcher.groupCount() > 0) {
      def line = matcher[0][1];
      def col =  matcher[0][2];
      def source =  matcher[0][3];
      return [endColumn : (col as int) + 1 + "",
	      endLine : line,
	      line : line,
	      message : message,
	      exceptionMessage : message,
	      sourceLocator : source,
	      startColumn : col,
	      startLine : line,
	      header: "Compile Error (" + cause.line +',' + cause.startColumn +")",
	      stackTrace : cause != null ? cause.getStackTrace() : ex.getStackTrace()];
    }
    return [message : message,
	    exceptionMessage : message,
	    header: "Compile Error",
	    stackTrace : cause != null ? cause.getStackTrace() : ex.getStackTrace()];
  }

  public GroovyParser(classloader) {
    this.classloader = classloader;
  }
  
  def parse(File f) {
    def code = new GroovyCodeSource(f);
    
    code.setCachable(false);
    try {
      return ["class": classloader.parseClass(code),
	      "errors" : []]
    } catch (org.codehaus.groovy.control.MultipleCompilationErrorsException ex){
      ex.printStackTrace();
      def rtnval = [];
      ErrorCollector collector = ex.getErrorCollector();
      ["class" : null,
       "errors" : collector.getErrors().collect( { handleException(it) }) ];
    }
  }

   
  def parse(String s) {
    try {
      return ["class": classloader.parseClass(s),
	      "errors" : []]
    } catch (org.codehaus.groovy.control.MultipleCompilationErrorsException ex){
      ex.printStackTrace();
      def rtnval = [];
      ErrorCollector collector = ex.getErrorCollector();
      ["class" : null,
       "errors" : collector.getErrors().collect( { handleException(it) }) ];
    }

  }

}
