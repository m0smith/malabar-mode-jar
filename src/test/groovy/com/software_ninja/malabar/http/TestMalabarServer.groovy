package com.software_ninja.malabar.project;

import static net.java.quickcheck.generator.PrimitiveGenerators.*
import static net.java.quickcheck.generator.CombinedGenerators.*
import static net.java.quickcheck.QuickCheck.*
import static net.java.quickcheck.generator.iterable.Iterables.*

import net.java.quickcheck.Generator;
import net.java.quickcheck.StatefulGenerator;
import net.java.quickcheck.collection.Pair;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;

import static org.junit.Assert.*

import com.sun.net.httpserver.HttpServer;

import com.software_ninja.malabar.MalabarUtil
import com.software_ninja.malabar.http.MalabarServer;

import groovy.io.FileType

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;    


class TestMalabarServer {

  private HttpServer server;

  private int port = 4426;

  private root = System.getProperty("user.dir").replace('\\', '/');
  
  @Before
  public void init() {
    server = new MalabarServer().start("" + port);
  }

  def readInputStreamAsString(InputStream input)   throws IOException {
    
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
    return new URL("http", "localhost", port,
		   "/" + context +"/?" +
		   args);
  }
  
  def createPostConnection(context, params) {

    def urlParameters =  params.collect({e -> e.toString()}).join("&");

    String request = "http://example.com/index.php";
    URL url = new URL("http", "localhost", port, '/' + context + '/');
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();           
    connection.setDoOutput(true);
    connection.setDoInput(true);
    connection.setInstanceFollowRedirects(false); 
    connection.setRequestMethod("POST"); 
    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
    connection.setRequestProperty("charset", "utf-8");
    connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
    connection.setUseCaches (false);
    
    DataOutputStream wr = new DataOutputStream(connection.getOutputStream ());
    wr.writeBytes(urlParameters);
    wr.flush();
    wr.close();
    connection.disconnect();
  }
  
  @Test
  public void testPi(){
    URL url = createGetURL("pi", [pm:root+"/src/test/projects/simple/pom.xml"]);
    println url
    InputStream is = url.openStream();
    println readInputStreamAsString(is);
    is.close();
    
  }

  public
  @Test void testParse(){
    URL url = createGetURL("parse", [pm:root + "/src/test/projects/simple/pom.xml",
				     script: root + "/src/test/projects/simple/src/test/java/com/software_ninja/test/project/AppTest.java" ]);

    println url
    InputStream is = url.openStream();

    url = createGetURL("parse", [pm : root + "/src/test/projects/simple/pom.xml",
				 scriptBody: "x=;" ]);
    is = url.openStream();
    println readInputStreamAsString(is);
    is.close();
  }

  @After
  public void destroy() {
    if(server != null) server.stop(1);
  }
  
}
