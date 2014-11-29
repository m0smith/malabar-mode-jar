package com.software_ninja.malabar.http;

import java.util.concurrent.Executors

def start() {
  port = 4428

  addr = new InetSocketAddress(port)
  httpServer = com.sun.net.httpserver.HttpServer.create(addr, 0)
  httpServer.with {
    createContext('/pi/', new JsonHandlerFactory().build({params -> new  com.software_ninja.malabar.project.MavenProject().projectInfo(params["repo"], params["pom"]);}));
    setExecutor(Executors.newCachedThreadPool())
    start()
  }
  println "running on " + port
}


