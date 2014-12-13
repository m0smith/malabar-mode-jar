package com.software_ninja.malabar;

/**
 *  A resource cache stores a "list" of CacheEntry of the contents of
 *  jar files to the path to the file.  The CacheEntry is a map from
 *  content element to paths
 **/

import java.util.zip.ZipFile;
 
class ResourceCache {
  def cache = [];
   
  def submit( String path ) {
    def ce = new CacheEntry(path);
    ZipFile file = new ZipFile(path)
    file.entries().each { entry ->  
      ce.add(entry.name);
    }  
    cache.add(ce);
  }

  def findInternal (pattern, max, asRegex, exclude) { 
    def rtnval = [];
    def sizeLeft = max;
    for( ele in cache ) {
      if(rtnval.size() >= max){
	break;
      }
      def l = ele.find(pattern, sizeLeft, asRegex, exclude);
      //println "L:" + l
      if(l.size() > 0 ){
	rtnval.addAll(l);

	//println "RTNVAL:" + rtnval
 	sizeLeft -= l.size();
      }
      
    }
    return rtnval;

  }


  /**
   * return a list of MapEntry where the key matches pattern
   */
  def find (pattern, max) { 
    return findInternal(pattern, max, true, null);
  }

  def findClass (pattern, max) { 
    return findInternal(pattern, max, true, '[.]class$|/');
  }
  
  def findExact (s, max) { 
    return findInternal(s, max, false, null);

  }
}

class CacheEntry {

  def cache = [];
  def value;

  public CacheEntry(String value) {
    this.value = value;
  }

  def add( key) {
    //println "KEY"+  key;
    cache.add(key);
    if(key.endsWith(".class")) { 
      cache.add( key.replaceAll(~ /.class$/, "").replace('/','.'))
    }
  }

  def entries() {
    return cache.collect({ it -> new CacheEntryEntry(it, value)});
  }

  def find(pattern, max, asRegex, exclude) {
    def rtnval = [];
    for( ele in cache ) {
      if(rtnval.size() >= max){
	break;
      }
      //println "FIND:[" + ele + "] [" + asRegex + "] " + pattern + " " 
      //println "FIND=:" + ele =~ pattern + " " + (ele =~ pattern && (exclude == null || !( ele =~ exclude)))
      if( asRegex ) {
	if(ele =~ pattern && (exclude == null || ! (ele =~ exclude))) {
	  rtnval.add( new CacheEntryEntry(ele, value));
	}
      } else {

	if(ele.equals(pattern) && (exclude == null || ! (ele =~ exclude))) {
	  rtnval.add( new CacheEntryEntry(ele, value));
	}

      }
    }
    return rtnval;

  }
  
}



class CacheEntryEntry {

  def key;
  def value;

  public CacheEntryEntry(key , value) {
    this.key = key;
    this.value = value;
  }

  public String getKey() { return key;}
  public String getValue() { return value;}
}
