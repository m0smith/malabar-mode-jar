package org.grumblesmurf.malabar;

import org.codehaus.plexus.logging.AbstractLoggerManager;
import org.codehaus.plexus.logging.Logger;

/**
 * 
 *
 * Created: 
 *
 * @author Matthew O. Smith
 * @since 2014
 */



public class MvnServerLoggerManager  extends AbstractLoggerManager
{
    private int currentThreshold;
    
    private Logger logger;

    public MvnServerLoggerManager ( Logger logger )
    {
        this.logger = logger;
    }

    @Override
    public int getActiveLoggerCount() {
        return 1;
    }


     public int getThreshold()
    {
        return currentThreshold;
    }

    public void setThreshold( String role,
                              String roleHint,
                              int threshold )
    {
    }

    public int getThreshold( String role,
                             String roleHint )
    {
        return currentThreshold;
    }
    public void setThreshold( int currentThreshold )
    {
        this.currentThreshold = currentThreshold;
    }

    public void setThresholds( int currentThreshold )
    {
        this.currentThreshold = currentThreshold;

        logger.setThreshold( currentThreshold );
    }
    public Logger getLoggerForComponent( String role,
                                         String roleHint )
    {
        return logger;
    }

    public void returnComponentLogger( String role,
                                       String roleHint )
    {
    }


} // MvnServerLoggerManager
