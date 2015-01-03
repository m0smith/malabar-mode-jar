package com.software_ninja.malabar.http;

import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpExchange
import groovy.json.JsonBuilder
import groovy.util.logging.*

@Log
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
	java.util.Map params = httpExchange.getAttribute("parameters");;
	  
	try {

	  httpExchange.responseHeaders.set('Content-Type', 'application/json')
	
	  //final String query = httpExchange.requestURI.rawQuery
	  log.fine "QUERY:" +  httpExchange.requestURI.rawQuery
	  log.fine "METHOD:" +  httpExchange.requestMethod
	  log.fine "PARAMS:" + params
	   
	  //log.fine params["repo"]
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
	  try {
	    throw new Exception( "Error handlinfg URL:" + httpExchange.requestURI.rawQuery +
				 " Method:" + httpExchange.requestMethod + 
				 " Params:" + params +
				 " Message: " + ex.getMessage(), ex);
	  } catch (Exception ex2) {
	    ex2.printStackTrace(new PrintStream(httpExchange.responseBody));
	    ex2.printStackTrace();x
	  }
	
	} finally {
	  httpExchange.close()
	  log.fine "end end end"
	}
      }
    }
  }


}
