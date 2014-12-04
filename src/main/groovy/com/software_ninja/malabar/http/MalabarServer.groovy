package com.software_ninja.malabar.http;

import java.util.concurrent.Executors

def start(String port) {

  addr = new InetSocketAddress(Integer.parseInt(port))
  httpServer = com.sun.net.httpserver.HttpServer.create(addr, 0)
  httpServer.with {
    createContext('/pi/', new JsonHandlerFactory().build({params -> new  com.software_ninja.malabar.project.MavenProjectHandler().projectInfo(params["repo"], params["pom"]);}));
    setExecutor(Executors.newCachedThreadPool())
    start()
  }
  println "running on " + port
}


