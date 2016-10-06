package com.software_ninja.malabar.project;

import org.gradle.tooling.model.idea.IdeaProject;
import org.gradle.tooling.model.idea.IdeaModule;
import org.gradle.tooling.GradleConnector;
//http://localhost:4429/pi/?pm=gradle&pmfile=c:/Users/lpmsmith/projects/malabar-mode-jar&repo=c:/Users/lpmsmith/.m2/repository


public class GradleProjectsCreator {


  public create( String repo, String pmfile){
    def connection = GradleConnector.newConnector().forProjectDirectory(new File(pmfile).getParentFile()).connect();
    println " /// Connected"
    def builder = connection.model(IdeaProject.class);
    println " /// Made builder"
    IdeaProject model = builder.get()
    println " /// got model"

    return model.getModules();
  }

  public resolveDependencies(  model,  _repo, scope) {
    def crs = model.contentRoots;
    def dependencies = model.dependencies;

    [ dependencies : dependencies.grep({ !it.exported } ).collect({ it.file.absolutePath }), 

      classpath : dependencies.collect({ it.file.absolutePath }),

      resources: [] ,

      sources:   "test" == scope ?  crs.collect({cr -> [cr.testDirectories.collect({it.directory.absolutePath}),
							cr.generatedTestDirectories.collect({it.directory.absolutePath})
							]}).flatten() 
      :
                                    crs.collect({cr -> [cr.sourceDirectories.collect({it.directory.absolutePath}),
							cr.generatedSourceDirectories.collect({it.directory.absolutePath})

						       ]}).flatten() ,


      elements:  ([ "test" == scope ? model.compilerOutput.testOutputDir : model.compilerOutput.outputDir ] - null).collect { it.toString() }
   ]
  }

  

}
