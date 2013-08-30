set dirname=%CD%
ECHO %dirname%
cd icasa.web
if exist .\RUNNING_PID del .\RUNNING_PID
java -DapplyEvolutions.default=true -cp "%dirname%\bin\*;%dirname%\lib\*" play.core.server.NettyServer "%dirname%"
