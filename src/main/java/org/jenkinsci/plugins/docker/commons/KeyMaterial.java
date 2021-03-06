package org.jenkinsci.plugins.docker.commons;

import hudson.EnvVars;
import hudson.model.AbstractBuild;
import org.jenkinsci.plugins.docker.commons.impl.CompositeKeyMaterial;
import org.jenkinsci.plugins.docker.commons.impl.NullKeyMaterial;

import javax.annotation.Nullable;
import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;

/**
 * Represents a locally extracted credentials information.
 *
 * <p>
 * Whenever you want to fork off docker directly or indirectly, use this object to set up environment variables
 * so that docker will talk to the right daemon.
 *
 * <p>
 * When you are done using the credentials, call {@link #close()} to allow sensitive information to be removed
 * from the disk.
 *
 * @author Kohsuke Kawaguchi
 * @see DockerServerEndpoint#materialize(AbstractBuild)
 * @see DockerRegistryEndpoint#materialize(AbstractBuild)
 */
public abstract class KeyMaterial implements Closeable, Serializable {
    /**
     * Builds the environment variables needed to be passed when docker runs, to access
     * {@link DockerServerCredentials} that this object was created from.
     */
    public abstract EnvVars env();

    /**
     * Deletes the key materials from the file system. As key materials are copied into files
     * every time {@link KeyMaterial} is created, it must be also cleaned up each time. 
     */
    public abstract void close() throws IOException;

    /**
     * Merge two {@link KeyMaterial}s into one.
     */
    public KeyMaterial plus(@Nullable KeyMaterial rhs) {
        if (rhs==null)  return this;
        return new CompositeKeyMaterial(this,rhs);
    }

    /**
     * {@link KeyMaterial} that does nothing.
     */
    public static final KeyMaterial NULL = new NullKeyMaterial();

    private static final long serialVersionUID = 1L;
}
