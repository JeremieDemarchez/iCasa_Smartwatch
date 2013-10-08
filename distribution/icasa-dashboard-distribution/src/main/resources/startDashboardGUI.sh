#!/usr/bin/env sh
cd web.dashboard
rm -f ./RUNNING_PID
exec java $* -Dfile.encoding=UTF8 -Dclient.encoding.override＝UTF-8 -Dhttp.port=9001 -DapplyEvolutions.default=true -cp "`dirname $0`/lib/*:`dirname $0`/bin/*" play.core.server.NettyServer `dirname $0`
