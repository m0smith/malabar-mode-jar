package org.grumblesmurf.malabar;

public interface MvnServerIntf {
    RunDescriptorIntf run(String pomFile, boolean recursive, String... goals) throws MalabarException;

}
