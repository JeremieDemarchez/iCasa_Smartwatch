ECHO %dirname%
cd web.simulator
set dirname=%CD%
if exist .\RUNNING_PID del .\RUNNING_PID
java -DapplyEvolutions.default=true -cp "%dirname%\bin\*;%dirname%\lib\*" play.core.server.NettyServer "%dirname%"
