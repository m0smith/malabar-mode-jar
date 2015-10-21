package com.software_ninja.malabar.project;

import com.software_ninja.malabar.MalabarUtil
import com.software_ninja.malabar.http.MalabarServer;
import com.sun.net.httpserver.HttpServer;
import groovy.io.FileType
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;    
import java.io.InputStream;
import net.java.quickcheck.Generator;
import net.java.quickcheck.StatefulGenerator;
import net.java.quickcheck.collection.Pair;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static net.java.quickcheck.QuickCheck.*
import static net.java.quickcheck.generator.CombinedGenerators.*
import static net.java.quickcheck.generator.PrimitiveGenerators.*
import static net.java.quickcheck.generator.iterable.Iterables.*
import static org.junit.Assert.*

class TestMalabarServer {

  private static HttpServer server;
  private static int port = 4426;

  @BeforeClass
  public static void init() {
    server = new MalabarServer().start("" + port);
  }
  @AfterClass
  public static void destroy() {
    if(server != null) server.stop(1);
  }  

  def readInputStreamAsString(InputStream input) throws IOException {
    
    BufferedInputStream bis = new BufferedInputStream(input);
    ByteArrayOutputStream buf = new ByteArrayOutputStream();
    int result = bis.read();
    while(result != -1) {
      byte b = (byte)result;
      buf.write(b);
      result = bis.read();
    }        
    return buf.toString();
  }

  def createGetURL(context, params) {
    def args =  params.collect({e -> e.toString()}).join("&");
    println "reateGetURL => " + args
    return new URL("http", "localhost", port, "/" + context +"/?" + args);
  }

  @Test
  public void testPi(){
    String pmfile = this.class.getClassLoader().getResource("projects/simple/pom.xml").getPath();
    println "pom.xml path => " + pmfile
    URL url = createGetURL('pi', ['pmfile': pmfile, 'pm':'maven']);
    InputStream is = url.openStream();
    println readInputStreamAsString(is);
    is.close();
  }

  @Test
  public void testParse(){

    String pmfile = this.class.getClassLoader().getResource("projects/simple/pom.xml").getPath();
    println "pom.xml path => " + pmfile

    String simpleTestPath =
      "projects/simple/src/test/java/com/software_ninja/test/project/";
    String scriptIn =
      this.class.getClassLoader().getResource(simpleTestPath + "AppTest.java").getPath();
    println "Test source path => " + scriptIn

    URL url = createGetURL('parse', 
			   [
			   'pmfile': pmfile,
			   'pm': 'maven',
			   'script': scriptIn
			    ]);
    InputStream is = url.openStream();

    url = createGetURL('parse', 
		       ['pmfile': pmfile,
		       'pm' : 'maven',
		       'scriptBody': 'x=;' 
			]
		       );

    is = url.openStream();
    println readInputStreamAsString(is);
    is.close();
  }
}
