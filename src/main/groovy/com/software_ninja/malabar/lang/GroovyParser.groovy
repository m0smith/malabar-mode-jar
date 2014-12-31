package com.software_ninja.malabar.lang;


public class GroovyParser implements Parser {

  def classloader;

  public GroovyParser(classloader) {
    this.classloader = classloader;
  }
  
  Class<?> parse(File f) {
    def code = new GroovyCodeSource(f);
    code.setCachable(false);
    classloader.parseClass(code);
  }
  
  Class<?> parse(String s) {
    classloader.parseClass(s);
  }

}
