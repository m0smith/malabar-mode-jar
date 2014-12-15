package com.software_ninja.malabar.http;

import java.util.concurrent.Executors
import com.software_ninja.malabar.project.MavenProjectHandler;
import com.software_ninja.malabar.MalabarUtil;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.Filter.Chain;

class MalabarServer {
  def cache = [:];
  def config = [ cache :cache ];
    
  def start(String port) {
    def mph = new MavenProjectHandler(config); 
    def addr = new InetSocketAddress(Integer.parseInt(port))
    def httpServer = com.sun.net.httpserver.HttpServer.create(addr, 0)

    def context = httpServer.createContext('/pi/', new JsonHandlerFactory(config).build({params ->
      def pmIn = params["pm"];
      def pm = (pmIn == null ? null : MalabarUtil.expandFile(pmIn));
      mph.projectInfo(params["repo"], pm);}));
    context.getFilters().add(new ParameterFilter());
    
    context = httpServer.createContext('/parse/', new JsonHandlerFactory(config).build({params ->
	def pmIn = params["pm"];
	def pm = (pmIn == null ? null : MalabarUtil.expandFile(pmIn));
	mph.parse(params["repo"], pm, params["script"], params["scriptBody"], params["strict"]);}));
    context.getFilters().add(new ParameterFilter());
    
    context = httpServer.createContext('/test/', new JsonHandlerFactory(config).build({params ->
	def pmIn = params["pm"];
	def pm = (pmIn == null ? null : MalabarUtil.expandFile(pmIn));
	mph.unitTest(params["repo"], pm, params["script"], params["method"]);}));
    context.getFilters().add(new ParameterFilter());
        
    context = httpServer.createContext('/resource/', new JsonHandlerFactory(config).build({params ->
	def pmIn = params["pm"];
	def pm = (pmIn == null ? null : MalabarUtil.expandFile(pmIn));
	mph.resource(params["repo"], pm, params["pattern"], params["max"] as int, params['isClass'] as boolean,
		     params['useRegex'] as boolean);}));
    context.getFilters().add(new ParameterFilter());
        
    context = httpServer.createContext('/add/', new JsonHandlerFactory(config).build({params ->
      println "ADD: " + params
      mph.additionalClasspath(params["relative"], params["abosulte"]);}));
    context.getFilters().add(new ParameterFilter());
        
    context = httpServer.createContext('/stop/', new JsonHandlerFactory(config).build({params ->  httpServer.stop(1); System.exit(0); }));
    context.getFilters().add(new ParameterFilter());
      
    httpServer.setExecutor(Executors.newCachedThreadPool())
    httpServer.start()

    println "running on " + port;
    return httpServer;
  }


}


class ParameterFilter extends Filter {

    @Override
    public String description() {
        return "Parses the requested URI for parameters";
    }

    @Override
    public void doFilter(HttpExchange exchange, Chain chain)
        throws IOException {
	  println exchange;
	  try {
	    parseGetParameters(exchange);
	    parsePostParameters(exchange);
	    println exchange.getAttribute("parameters");
	  } catch (Exception ex) {
	    ex.printStackTrace();
	  } finally {
	    chain.doFilter(exchange);
	  }
    }    

    private void parseGetParameters(HttpExchange exchange)
        throws UnsupportedEncodingException {

        Map<String, Object> parameters = new HashMap<String, Object>();
        URI requestedUri = exchange.getRequestURI();
        String query = requestedUri.getRawQuery();
        parseQuery(query, parameters);
        exchange.setAttribute("parameters", parameters);
    }

    private void parsePostParameters(HttpExchange exchange)
        throws IOException {

        if ("post".equalsIgnoreCase(exchange.getRequestMethod())) {
            @SuppressWarnings("unchecked")
            Map<String, Object> parameters =
                (Map<String, Object>)exchange.getAttribute("parameters");
            InputStreamReader isr =
                new InputStreamReader(exchange.getRequestBody(),"utf-8");
            BufferedReader br = new BufferedReader(isr);
            String query = br.readLine();
            parseQuery(query, parameters);
        }
    }

     @SuppressWarnings("unchecked")
     private void parseQuery(String query, Map<String, Object> parameters)
         throws UnsupportedEncodingException {

         if (query != null) {
             String[] pairs = query.split("[&]");

             for (String pair : pairs) {
                 String[] param = pair.split("[=]");

                 String key = null;
                 String value = null;
                 if (param.length > 0) {
                     key = URLDecoder.decode(param[0],
                         System.getProperty("file.encoding"));
                 }

                 if (param.length > 1) {
                     value = URLDecoder.decode(param[1],
                         System.getProperty("file.encoding"));
                 }

                 if (parameters.containsKey(key)) {
                     Object obj = parameters.get(key);
                     if(obj instanceof List<?>) {
                         List<String> values = (List<String>)obj;
                         values.add(value);
                     } else if(obj instanceof String) {
                         List<String> values = new ArrayList<String>();
                         values.add((String)obj);
                         values.add(value);
                         parameters.put(key, values);
                     }
                 } else {
                     parameters.put(key, value);
                 }
             }
         }
    }
}
