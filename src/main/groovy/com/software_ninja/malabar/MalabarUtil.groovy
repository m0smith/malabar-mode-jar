
package com.software_ninja.malabar;

import java.util.logging.Logger;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;

public class MalabarUtil {

  /**
   * Expand a file name.  Replaces ~ at the begining of the string to $HOME
   */
  public static expandFile(f) {
    if(f.startsWith('~')) {
      return System.getProperty("user.home") + f.substring(1);
    }
    return f;
  }


  /**
   * Set logging level.
   */

   public static setLevel(Class clazz, Level level){
     setLevel(clazz.getName(), level);
   }

   public static setLevel(String logger, Level level){
     setLevel(LogManager.logManager.getLogger(logger), level);
   }

   public static setLevel(Logger logger, Level level){
     logger.setLevel(level);
     logger.setUseParentHandlers(false);
     ConsoleHandler handler = new ConsoleHandler();
     handler.setLevel(level);
     logger.addHandler(handler);
     logger.handlers.each( { it.setLevel(level) }); 
  }

  
}

