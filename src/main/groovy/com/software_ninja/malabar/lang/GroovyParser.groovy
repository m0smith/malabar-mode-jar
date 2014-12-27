package com.software_ninja.malabar.lang;


public class GroovyParser implements Parser {

  def classloader;

  public GroovyParser(classloader) {
    this.classloader = classloader;
  }
  
  Class<?> parse(File f) {
    classloader.parseClass(f);
  }
  
  Class<?> parse(String s) {
    classloader.parseClass(s);
  }

}
