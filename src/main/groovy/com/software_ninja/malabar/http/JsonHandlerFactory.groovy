package com.software_ninja.malabar.http;

import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpExchange
import groovy.json.JsonBuilder


class JsonHandlerFactory {

  private Map cache;
  
  public JsonHandlerFactory(config) {
    this.cache = config['cache'];
  }
  
  def queryMap (query) {
    def rtnval = [:]
    if(query == null) return rtnval;

    def params = query.split('&')
    params.each{ kv -> 
      def eqat = kv.indexOf('='); 
      if(eqat >= 0) { 
	rtnval[kv.substring(0, eqat)] = kv.substring(eqat + 1);
      } 
      else 
      {
	rtnval[kv] = '';
      } 
    }
  
    rtnval
  }


  def build (func) {

    return new HttpHandler(){
      public void handle(HttpExchange httpExchange) throws IOException
      {
	try {

	  httpExchange.responseHeaders.set('Content-Type', 'application/json')
	  java.util.Map params = httpExchange.getAttribute("parameters");;
	
	  //final String query = httpExchange.requestURI.rawQuery
	  println params
	   
	  //println params["repo"]
	  // if(!query || !query.contains('string')){
	  // 	httpExchange.sendResponseHeaders(400,0)
	  // 	return
	  // }
	
	  //final String[] param = query.split('=')
	  //assert param.length == 2 && param[0] == 'string'
	  def bytes = new JsonBuilder( func(params) ).toPrettyString().bytes;
	
	  httpExchange.sendResponseHeaders(200, 0)
	  httpExchange.responseBody.write( bytes )
	
	} catch (Throwable ex) {
	  httpExchange.sendResponseHeaders(500, 0)
	  println org.codehaus.groovy.runtime.StackTraceUtils.printSanitizedStackTrace(ex)
	  ex.printStackTrace(new PrintStream(httpExchange.responseBody));
	
	} finally {
	  httpExchange.close()
	  println "end end end"
	}
      }
    }
  }


}
