package com.software_ninja.malabar.lang;

public interface Parser {
  /**
   * Parse the file and return a map of ["class" "errors"] where class
   * is the class that is created by parsing file.  "errors" is the list
   * of error information for all compilation errors.
   */
  def parse(File f); 

  /**
   * Parse the file and return a map of ["class" "errors"] where class
   * is the class that is created by parsing s.  "errors" is the list
   * of error information for all compilation errors.
   */
  def parse(String s);

}
