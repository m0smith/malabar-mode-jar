package com.software_ninja.malabar.lang;

public interface Parser {

  Class<?> parse(File f);

  Class<?> parse(String s);

}
