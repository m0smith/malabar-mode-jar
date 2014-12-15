package com.software_ninja.malabar;

import static net.java.quickcheck.generator.PrimitiveGenerators.*
import static net.java.quickcheck.generator.CombinedGenerators.*
import static net.java.quickcheck.QuickCheck.*
import static net.java.quickcheck.generator.iterable.Iterables.*

import net.java.quickcheck.Generator;
import net.java.quickcheck.StatefulGenerator;
import net.java.quickcheck.collection.Pair;

import org.junit.Test;
import org.junit.Before;

import static org.junit.Assert.*

import com.software_ninja.malabar.ResourceCache;
import com.software_ninja.malabar.MalabarUtil;
       
/**
 *  A resource cache stores a "map" of the contents of jar files to the path to the file.
 **/
   
class TestResourceCacheImpl {
    private String jar = MalabarUtil.expandFile("~/.m2/repository/xalan/xalan/2.7.1/xalan-2.7.1.jar"); 
  @Test
  public void testCache() {
    def rc = new ResourceCache();
    rc.submit(jar);
    String name = "org/apache/xpath/XPathVisitor.class";
    assertEquals (name, rc.find(name,10).first().getKey());
    name = "org.apache.xpath.XPathVisitor";
    assertEquals (name, rc.findExact(name,10).first().getKey());

    name = "org.apache.xpath.jaxp.JAXPVariableStack";
    assertEquals (name, rc.findClass(name,10).first().getKey());

  } 


 @Test
  public void testFindClass() {
    def rc = new ResourceCache();
    rc.submit(jar);
    def name = /XP.*V.*[^.]*$/;
    assertEquals ("org.apache.xpath.jaxp.JAXPVariableStack", rc.findClass(name,10).first().getKey());
  }
} 

 
 
