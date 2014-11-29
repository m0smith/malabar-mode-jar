package com.software_ninja.malabar.http;

import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpExchange
import groovy.json.JsonBuilder




def queryMap (query) {
  def rtnval = [:]
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
  println rtnval.getClass().getName()
  rtnval
}


def build (func) {

  return new HttpHandler(){
    public void handle(HttpExchange httpExchange) throws IOException
    {
      try {
	
	httpExchange.responseHeaders.set('Content-Type', 'text/plain')
	final String query = httpExchange.requestURI.rawQuery
	println query
	final java.util.Map params = queryMap(query)
	println params["repo"]
	// if(!query || !query.contains('string')){
	// 	httpExchange.sendResponseHeaders(400,0)
	// 	return
	// }
	
	//final String[] param = query.split('=')
	//assert param.length == 2 && param[0] == 'string'
	httpExchange.sendResponseHeaders(200, 0)
	httpExchange.responseBody.write( new JsonBuilder( func(params) ).toPrettyString().bytes )
	httpExchange.responseBody.close()
      } catch (Throwable ex) {
	println org.codehaus.groovy.runtime.StackTraceUtils.printSanitizedStackTrace(ex)
	ex.printStackTrace();
	
      } finally {
	println "end of handler"
      }
    }
  }
}


