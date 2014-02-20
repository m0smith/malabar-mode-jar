package org.grumblesmurf.malabar;

import java.io.File;

import java.util.Collections;
import java.util.List;

import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.apache.maven.shared.invoker.PrintStreamHandler;
/**
 *
 */
public class InvokerMvnServer implements MvnServerIntf {

    private String projectDir;

    private List<String>goals;

    public InvokerMvnServer(String projectDir, List<String> goals) {
        this.projectDir = projectDir;
        this.goals = goals;
    }

    public boolean run () throws MalabarException {

        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile( new File( projectDir + "/pom.xml" ) );
        request.setGoals( goals );        
        request.setOutputHandler(new PrintStreamHandler());
    
        Invoker invoker = new DefaultInvoker();    
        try {
            InvocationResult result = invoker.execute(request);
            Throwable rtnval = result.getExecutionException();
            int code = result.getExitCode();
            if(rtnval == null && code == 0 ){
                return true;
            } else {
                //throw new MalabarException("Compile failed with projectDir:" + projectDir + " code:" + code, rtnval);
                return false;
            }

        } catch (MavenInvocationException ex ) {
            throw new MalabarException ("Failed to invoke build on : " + projectDir, ex);
        }
    }


}
