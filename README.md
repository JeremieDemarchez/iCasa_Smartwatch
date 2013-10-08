iCasa-Apps
==========

Contains a set of ready-to-run iCasa applications and demonstrations.
The home page of the project is <http://adeleresearchgroup.github.com/iCasa-Apps/>.
The project websites are:
 * Development version <http://adeleresearchgroup.github.com/iCasa-Apps/snapshot/>
 * Last stable release version <http://adeleresearchgroup.github.com/iCasa-Apps/1.2.1/>

License
=====

This project relies on Apache v2 license (<http://www.apache.org/licenses/LICENSE-2.0.html>).

Maven Repository
=============


    <repositories>
        <repository>
          <snapshots>
            <enabled>false</enabled>
          </snapshots>
          <id>adele-central-snapshot</id>
          <name>adele-repos</name>
          <url>http://maven.dynamis-technologies.com/artifactory/adele-repos</url>
        </repository>
        <repository>
          <snapshots />
          <id>snapshots</id>
          <name>adele-central-release</name>
          <url>http://maven.dynamis-technologies.com/artifactory/adele-repos</url>
        </repository>
    </repositories>
    <pluginRepositories>
        <pluginRepository>
          <snapshots>
            <enabled>false</enabled>
          </snapshots>
          <id>central</id>
          <name>adele-repos</name>
          <url>http://maven.dynamis-technologies.com/artifactory/adele-repos</url>
        </pluginRepository>
        <pluginRepository>
          <snapshots />
          <id>snapshots</id>
          <name>adele-central-release</name>
          <url>http://maven.dynamis-technologies.com/artifactory/adele-repos</url>
        </pluginRepository>
    </pluginRepositories>

Build
=====

Prerequisites
-----

- install Maven 3.x
- install jdk 6 or upper

Instructions
----

Use the following command to compile the project
> mvn clean install

Continuous Integration
----

The project is built every week on the following continuous integration server :
<https://icasa.ci.cloudbees.com/>

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
- All packages must start with fr.liglab.adele.icasa.apps
- All Maven artifact group id must be fr.liglab.adele.icasa.apps
- All maven artifact id must not contain fr.liglab.adele.icasa.apps and must be lower case (cannot use underscore, prefer dots)
- All maven project pom.xml file must inherent from parent pom (group id = fr.liglab.adele.icasa.apps and artifact id = parent)
