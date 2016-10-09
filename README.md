# malabar-mode-jar
 A support JAR for malabar-mode: JVM Integration minor mode for EMACS

See malabar-mode: https://github.com/m0smith/malabar-mode

# Configuration

It may be necessary to configure grape `~/.groovy/grapeConfig.xml` see [gh-30](https://github.com/m0smith/malabar-mode-jar/issues/30):

```

<ivysettings>
    <settings defaultResolver="downloadGrapes"/>
    <resolvers>
        <chain name="downloadGrapes">
            <!-- todo add 'endorsed groovy extensions' resolver here -->
            <ibiblio name="local" root="file:${user.home}/.m2/repository/" m2compatible="true"/>
            <filesystem name="cachedGrapes">
                <ivy pattern="${user.home}/.groovy/grapes/[organisation]/[module]/ivy-[revision].xml"/>
                <artifact pattern="${user.home}/.groovy/grapes/[organisation]/[module]/[type]s/[artifact]-[revision].[ext]"/>
            </filesystem>
            <ibiblio name="codehaus" root="http://repository.codehaus.org/" m2compatible="true"/>
            <ibiblio name="ibiblio" m2compatible="true"/>
            <ibiblio name="java.net2" root="http://download.java.net/maven/2/" m2compatible="true"/>
        </chain>
    </resolvers>
</ivysettings>
```

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
    Map[] grapez = [[group: 'com.software-ninja' , module:'malabar', version:'2.3.1']]
    groovy.grape.Grape.grab(classLoader: classLoader, grapez)
    classLoader.loadClass('com.software_ninja.malabar.MalabarStart').newInstance().startCL(classLoader); }; 
malabar();
```

Be sure the version in the second line is set to the lastest in central: http://search.maven.org/#search|gav|1|g%3A%22com.software-ninja%22%20AND%20a%3A%22malabar%22

## Ports

These are the ports used by default for each mode of running:

- unit test: 4426
- groovysh: 4428
- standalone: 4429


# Services
malabar-mode-jar provides REST-ish services for interacting with the JVM and package management software (like maven)

## add
The add services adds additional classpath entries to the project's classloader

### Parameters

| name | default | desc 
|------|---------|------
| repo | none    | The location of the maven repo (something like /home/username/.m2/repository)
| pm  | none    | The location of the pm to parse
| relative  | none    | classpath entries relative to the project root dir, as a JSON list of strings
| absolute  | none    | absolute path classpath entries  as a JSON list of strings

## pi
The pi services returns information from the maven pom.

### Parameters

| name | default | desc 
|------|---------|------
| repo | none    | The location of the maven repo (something like /home/username/.m2/repository)
| pm  | none    | The location of the pm to parse


## parse
The parse services parses a source file or block of source code and returns a list of errors

### Parameters

| name | default | desc 
|------|---------|------
| repo | ~/.m2/repository    | The location of the maven repo (something like /home/username/.m2/repository)
| pm  | none    | The location of the pm to parse
| script  | none    | The path on disk to the script to parse
| scriptBody  | none    | The actual script to parse
| strict  | none    | If true, force the parse to strict/static compilation

## resource
The resource services provides access to classes and other resources on the project classpath

### Parameters

| name | default | desc 
|------|---------|------
| repo | ~/.m2/repository    | The location of the maven repo (something like /home/username/.m2/repository)
| pm  | none    | The location of the pm to parse
| pattern  | none    | The pattern to search for
| max  | none    | The maximum number of results to return
| isClass  | true   | If true, only search for classes, If false, also return files. 
| useRegex  | true    | If true, treat pattern as a regex.  Otherwise, just treat it as a substring

## stop
The tags service shutsdown the http server and the JVM using System.exit.

### Parameters

| name | default | desc 
|------|---------|------

## tags
The tags service returns the semantic tags for a class

### Parameters

| name | default | desc 
|------|---------|------
| repo | ~/.m2/repository    | The location of the maven repo (something like /home/username/.m2/repository)
| pm  | none    | The location of the pm to parse
| class  | none    | The binary or fully-qualified class name to reflect


## test
The test service runs a unit test on either a whole class or just a single method

### Parameters

| name | default | desc 
|------|---------|------
| repo | ~/.m2/repository    | The location of the maven repo (something like /home/username/.m2/repository)
| pm  | none    | The location of the pm to parse
| script  | none    | The path on disk to the script to parse
| method  | none    | If not null, the name of the method to run.  If null, null all tests in the class


# Hacking

All development is done on the 'master' branch in as part of a
SNAPSHOT.  When a release needs to happen, the maven release plugin is
used to tag the build and push it to maven central.

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
