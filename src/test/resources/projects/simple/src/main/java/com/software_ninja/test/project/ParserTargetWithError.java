 package com.software_ninja.test.project;

/**
 * Hello world!
 *
 */ 

class PreParserTarget{}
 
interface PreInterface {}

public class ParserTargetWithError
{ 
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
    }

    
    public boolean getTrue() {
	return true;
    }

    PreInterface pi = new PreInterface(){};

    class Inner {}

    static class StaticInner{ x=;}

}

