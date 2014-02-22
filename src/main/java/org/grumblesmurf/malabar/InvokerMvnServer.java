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
import java.util.Arrays;

/**
 *
 */
public class InvokerMvnServer implements MvnServerIntf{

    private String projectDir;

    private List<String>goals;

    public InvokerMvnServer(String projectDir) {
        this.projectDir = projectDir;
        this.goals = goals;
    }


    public RunDescriptorIntf run (String pomfile, boolean recursive, String... goals) throws MalabarException {

        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile( new File( pomfile ) );
        request.setGoals( Arrays.asList(goals) );        
        request.setOutputHandler(new PrintStreamHandler());
        request.setRecursive(recursive);
    
        return new RunDescriptorImpl(request);
    }

    /**
     * Create the run description for this porject.
     */
    public class RunDescriptorImpl implements RunDescriptorIntf {
        private InvocationRequest request;

        public RunDescriptorImpl(InvocationRequest request) {
            this.request = request;
        }

        public boolean run() throws MalabarException {
            Invoker invoker = new DefaultInvoker();    
            try {
                InvocationResult result = invoker.execute(request);
                Throwable rtnval = result.getExecutionException();
                int code = result.getExitCode();
                if(rtnval == null && code == 0 ){
                    return true;
                } else {
                    // throw new MalabarException("Compile failed with projectDir:" + projectDir + " code:" + code, rtnval);
                    return false;
                }

            } catch (MavenInvocationException ex ) {
                throw new MalabarException ("Failed to invoke build on : " + projectDir, ex);
            }
        }
    }
}
