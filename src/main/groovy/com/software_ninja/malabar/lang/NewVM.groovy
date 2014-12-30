package com.software_ninja.malabar.lang;


public class NewVM {

  public static void startSecondJVM(String version, String jdkPath, String port, String cwd, boolean redirectStream) throws Exception {
    //System.out.println(clazz.getCanonicalName());
    def grapez = [group: 'com.software-ninja' , module:'malabar', version:version]; 
    def classLoader = new groovy.lang.GroovyClassLoader(); 
    groovy.grape.Grape.grab(classLoader: classLoader, grapez);

    String separator = System.getProperty("path.separator");
    String classpath = classLoader.classPath.join(separator)
    def ii = jdkPath + "bin/javaw.exe"
    //println(classpath);
    ProcessBuilder processBuilder =   new ProcessBuilder(ii, "-cp", 
							 classpath,
							 '-Xmx128m',
							 'com.software_ninja.malabar.Malabar',
							 "-p", 
							 port);
    processBuilder.directory(new File(cwd));
    processBuilder.redirectErrorStream(redirectStream);
    processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
    processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);
    final Process process = processBuilder.start();
    Thread closeChildThread = new Thread() {
      public void run() {
        process.destroy();
      }
    };

    Runtime.getRuntime().addShutdownHook(closeChildThread); 
    //process.waitFor();
    //System.out.println("Fin");
  }
}


