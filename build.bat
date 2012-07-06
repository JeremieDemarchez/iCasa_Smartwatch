@echo --- --------------------------------------------------------------- ---
@echo --- Install akquinet OSGi Deployment admin implementation and tools ---
@echo --- --------------------------------------------------------------- ---
@set MAVEN_OPTS=-Xms512m -Xmx1024m -XX:PermSize=256m -XX:MaxPermSize=512m
@cd dependencies
@cd osgi-deployment-admin
@cmd /C mvn clean install
@cd ..
@cd ..
@echo --- --------------------------------------------------------------- --- 
@echo ---                       Build And Install ICasa                    ---
@echo --- --------------------------------------------------------------- ---
@cmd /C mvn clean install
