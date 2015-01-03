package com.software_ninja.malabar;


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

  
}

