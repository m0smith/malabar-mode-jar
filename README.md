# malabar-mode-jar
 A support JAR for malabar-mode: better Java mode for Emacs

See malabar-mode: https://github.com/m0smith/malabar-mode

# Running

## Standalone

```
gradle run
```

or to set the port

```
gradle -Dexec.args="-p 4429" run
```

## groovysh


```groovy
def malabar = { classLoader = new groovy.lang.GroovyClassLoader();
    Map[] grapez = [[group: 'com.software-ninja' , module:'malabar', version:'2.0.2']]
    groovy.grape.Grape.grab(classLoader: classLoader, grapez)
    classLoader.loadClass('com.software_ninja.malabar.Malabar').newInstance().startCL(classLoader); }; 
malabar();
```

Be sure the version in the second line is set to the lastest in central: http://search.maven.org/#search|gav|1|g%3A%22com.software-ninja%22%20AND%20a%3A%22malabar%22

# Services
malabar-mode-jar provides REST-ish services for interacting with the JVM and package management software (like maven)

## pi
The pi services returns information from the maven pom.
### Parameters
| name | default | desc 
|------|---------|------
| repo | none    | The location of the maven repo (something like /home/username/.m2/repository)
| pom  | none    | The location of the pom to parse



# Hacking

All development is done on the 'master' branch in as part of a SNAPSHOP.  When a release needs to happen, the maven release plugin is used to tag the build and push it to maven central.

# Boring legal stuff

malabar-mode-jar is copyright (c) 2009-2010 Espen Wiborg <espenhw@grumblesmurf.org>

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 2 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
General Public License for more details.

For the full text of the GPL, see <http://www.gnu.org/licenses/gpl2.txt>.

[JDEE]: http://jdee.sourceforge.net/
[run jdb on an applet]: http://jdee.sourceforge.net/jdedoc/html/jde-ug/jde-ug-content.html#d0e4142
[BeanShell]: http://www.beanshell.org/
[my blog]: http://blog.grumblesmurf.org/
[Maven]: http://maven.apache.org/
[CEDET]: http://cedet.sourceforge.net/
[Groovy]: http://groovy.codehaus.org/
[Junit]: http://www.junit.org/
[issue tracker]: http://github.com/dstu/malabar-mode/issues
[Nikolaj Schumacher]: http://nschum.de/src/emacs/
[standard Semantic code completion]: http://cedet.sourceforge.net/intellisense.shtml
[Elvis operator]: http://groovy.codehaus.org/Operators#Operators-ElvisOperator
[git-flow]: http://nvie.com/posts/a-successful-git-branching-model/
