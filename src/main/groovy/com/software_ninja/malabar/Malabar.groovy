


package com.software_ninja.malabar;

import groovy.grape.Grape

public static void main(String[] args) {
  new MalabarStart().startArgs(args);
}


class MalabarStart {

  private static String defaultPort = '4428';
  
  def startArgs(String[] args) {
    println "ARGS: " + args
    def argMap = new SpreadMap(args);
    String port = defaultPort;
    if(argMap["-p"] != null) port = argMap["-p"];
    println "Starting with :" + argMap
    startP(port);
    
  }
  
  def start (){
    ClassLoader classLoader = new groovy.lang.GroovyClassLoader()
    startCLP(classLoader, defaultPort);
  }
  
  def startP (String port){
    ClassLoader classLoader = new groovy.lang.GroovyClassLoader()
    startCLP(classLoader, port);
  }
  
  def startCL (ClassLoader classLoader1){
    startCLP(classLoader1, defaultPort);
  }
  
  def startCLP (ClassLoader classLoader1, String port){
    
    Map[] grapez = [
      [group : 'org.apache.maven', module : 'maven-compat', version :'3.0.5'],
      [group : 'com.jcabi', module : 'jcabi-aether', version : '0.10.1']];
    Grape.grab(classLoader: classLoader1, grapez)
    //println "Class: " + classLoader.loadClass('org.ccil.cowan.tagsoup.jaxp.SAXParserImpl')
    classLoader1.loadClass('com.software_ninja.malabar.http.MalabarServer').newInstance().start(port);
    
  }
}


//http://localhost:4428/pi/repo=c:/users/Smith/.m2/repository&pom=c:/Users/Smith/projects/malabar-mode-jar/pom.xml
