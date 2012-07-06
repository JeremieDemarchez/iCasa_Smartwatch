!!! YOU MUST execute following commands for the first build !!!
cd dependencies
cd osgi-deployment-admin
mvn clean install

To build complete repository, execute mvn install from this directory
or you can use the script file build.bat or build.

Prerequisites:
- install Maven 3.x
- install jdk 6 (NOT java 7 !!!)

If you want to contribute to this project, you MUST follow the developper guidelines:
- Use Sun naming convention in your code.
- You should prefix private class member by an underscore (e.g. : _bundleContext).
- All project directory names must be lower case without dots (you can use - instead of underscores).
- All packages must start with fr.liglab.adele.icasa
- All Maven artifact group id must be fr.liglab.adele.icasa
- All maven artifact id must not contain fr.liglab.adele.icasa and must be lower case (cannot use underscore, prefer dots)
- All maven project pom.xml file must inherent from parent pom (group id = fr.liglab.adele.icasa and artifact id = parent)
