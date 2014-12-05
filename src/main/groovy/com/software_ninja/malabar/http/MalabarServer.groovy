package com.software_ninja.malabar.http;

import java.util.concurrent.Executors
import com.software_ninja.malabar.project.MavenProjectHandler;
import com.software_ninja.malabar.MalabarUtil;
import com.sun.net.httpserver.HttpHandler

class MalabarServer {
  def cache = [:];
  def config = [ cache :cache ];
  
  def start(String port) {
    def mph = new MavenProjectHandler(config); 
    def addr = new InetSocketAddress(Integer.parseInt(port))
    def httpServer = com.sun.net.httpserver.HttpServer.create(addr, 0)
    httpServer.with {

      createContext('/pi/', new JsonHandlerFactory(config).build({params ->
	def pomIn = params["pom"];
	def pom = (pomIn == null ? null : MalabarUtil.expandFile(pomIn));
	mph.projectInfo(params["repo"], pom);}));

      createContext('/parse/', new JsonHandlerFactory(config).build({params ->
	def pomIn = params["pom"];
	def pom = (pomIn == null ? null : MalabarUtil.expandFile(pomIn));
	mph.parse(params["repo"], pom, params["script"]);}));
      
      createContext('/stop/', new JsonHandlerFactory(config).build({params ->  httpServer.stop(1); System.exit(0); }));
      
      setExecutor(Executors.newCachedThreadPool())
      start()
    }
    println "running on " + port
  }
}


