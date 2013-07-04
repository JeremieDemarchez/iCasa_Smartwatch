iCasa-Simulator
=====

This project aims at providing a simulator to do functional testing of digital home application based on iCasa runtime.
The home page of the project is <http://adeleresearchgroup.github.com/iCasa-Simulator/>.
The project websites are:
 * Development version <http://adeleresearchgroup.github.com/iCasa-Simulator/snapshot/>
 * Last stable release version <http://adeleresearchgroup.github.com/iCasa-Simulator/1.1.0/>

License
=====

This project relies on Apache v2 license (<http://www.apache.org/licenses/LICENSE-2.0.html>).

Contributors
=====

This project has been created by the LIG laboratory ADELE Reseach Group of Grenoble University.
The main contributors are 
- _ADELE Reseach Group_
- _Orange Labs_

Source Organization
====

- _distribution_: Contains ready to use distributions of client and server simulator parts.
- _packages_: Contains deployment packages of client and server simulator parts.
- _ihm_: Contains android applications and a client web application to use the simulator.
- _parent_: Contains the global build configuration including licensing information.
- _simulator_: Contains server part of the simulator.
- _src_: Contains the project web site source code.

How to use it
=====

Use a distribution
----

1. Install jdk 6 (NOT java 7 !!!)
2. Unzip one of the distribution
3. Execute startGateway file on Unix (or startGateway.bat file on Windows)

Build a new distribution
----

1. Install all build prerequisites (described in next section).
2. Then add the following maven repositories to your project pom.xml file.
```xml
<repository>
	<id>maven-icasa-repository-release</id>
	<name>iCasa - Release</name>
	<url>https://repository-icasa.forge.cloudbees.com/release/</url>
	<layout>default</layout>
</repository>
<repository>
	<id>maven-icasa-repository-snapshot</id>
	<name>iCasa - Snapshot</name>
	<url>https://repository-icasa.forge.cloudbees.com/snapshot/</url>
	<layout>default</layout>
</repository>
```
3.

Build
=====

Prerequisites
-----

- install Maven 3.x
- install jdk 6 (NOT java 7 !!!)

Instructions
----

Use the following command to compile the project
> mvn clean install

Continuous Integration
----

The project is built every week on the following continuous integration server :
<https://icasa.ci.cloudbees.com/>

Maven Repositories
----

```xml
<repository>
	<id>maven-icasa-repository-release</id>
	<name>iCasa - Release</name>
	<url>https://repository-icasa.forge.cloudbees.com/release/</url>
	<layout>default</layout>
</repository>
<repository>
	<id>maven-icasa-repository-snapshot</id>
	<name>iCasa - Snapshot</name>
	<url>https://repository-icasa.forge.cloudbees.com/snapshot/</url>
	<layout>default</layout>
</repository>
```

Contribute to this project
====

Released Version semantic
----

 major.minor.revision 

 * _major_ changed when there are modification or addition in the functionalities. 
 * _minor_ changed when minor features or critical fixes have been added.
 * _revision_ changed when minor bugs are fixed.

Developer Guidelines
----
 
If you want to contribute to this project, you MUST follow the developper guidelines:
- Use Sun naming convention in your code.
- You should prefix private class member by an underscore (e.g. : _bundleContext).
- All project directory names must be lower case without dots (you can use - instead of underscores).
- All packages must start with fr.liglab.adele.icasa
- All Maven artifact group id must be fr.liglab.adele.icasa
- All maven artifact id must not contain fr.liglab.adele.icasa and must be lower case (cannot use underscore, prefer dots)
- All maven project pom.xml file must inherent from parent pom (group id = fr.liglab.adele.icasa and artifact id = parent)
