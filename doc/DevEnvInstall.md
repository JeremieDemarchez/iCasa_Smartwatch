Development Environment Install Guide
-------
This document explains how to setup a development environment for iCasa applications

Prerequisites
-------

- IDE : Eclipse (>=Juno) ou IntelliJ (>= 11)
- Build system : Maven  >=3.0.4
- SCM : Git >= 1.8.0
- Java : JDK >= 6 (JDK 7 may not working in all cases, so prefer jdk 6)

For Eclipse :
- Maven plugin (included in Eclipse if you download Eclipse IDE for Java Developpers)
- PDE plugin (in General purpose category)

Optional
--------

For easy github repository cloning
- GitHub software
On windows
- Tortoise Git
For web development :
- Chrome
For SOAP and REST development :
- SOAP UI (SOAP)
- curl (REST)
For easy text file editing on Windows:
- Notepad++

Recommended directory structure
--------

create a dev directory:
dev
-eclipse
-maven
-maven-repo
-workspaces
--iCasa-Platform
--MyProject
...

Configuration
---------

1. Open conf/settings.xml file of Maven and setup the maven local repository to your dev/maven-repo directory
2. Add dev/maven/bin to the PATH environment variable
3. Set JAVA_HOME to your jdk 6 directory
4. Set MAVEN_OPTS to -Xms512m -Xmx1024m -XX:PermSize=256m -XX:MaxPermSize=512m

Troobleshootings
-------

- You probably need 64 bit version of softwares
- If you get some transfer or missing dependency errors, check that your internet proxy is defined in the maven settings.xml file