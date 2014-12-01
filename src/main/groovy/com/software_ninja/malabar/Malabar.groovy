


package com.software_ninja.malabar;

import groovy.grape.Grape

public static void main(String[] args) {
  start();
}

def start (){
    ClassLoader classLoader = new groovy.lang.GroovyClassLoader()
    Map[] grapez = [
		    [group : 'org.apache.maven', module : 'maven-compat', version :'3.0.5'],
		    [group : 'com.jcabi', module : 'jcabi-aether', version : '0.10.1']];
    Grape.grab(classLoader: classLoader, grapez)
    //println "Class: " + classLoader.loadClass('org.ccil.cowan.tagsoup.jaxp.SAXParserImpl')
    classLoader.loadClass('com.software_ninja.malabar.http.MalabarServer').newInstance().start();
    
}


//http://localhost:4428/pi/repo=c:/users/Smith/.m2/repository&pom=c:/Users/Smith/projects/malabar-mode-jar/pom.xml
