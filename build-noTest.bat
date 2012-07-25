@echo --- --------------------------------------------------------------- --- 
@echo ---                  Build And Install ICasa -=SkipTests=-          ---
@echo --- --------------------------------------------------------------- ---
@set MAVEN_OPTS=-Xms512m -Xmx1024m -XX:PermSize=256m -XX:MaxPermSize=512m
@cmd /C mvn clean install -DskipTests
