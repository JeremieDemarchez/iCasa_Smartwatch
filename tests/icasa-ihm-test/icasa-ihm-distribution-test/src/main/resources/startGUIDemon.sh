#!/bin/bash
ICASA_COMMAND="java $* -DapplyEvolutions.default=true -cp "`dirname $0`/lib/*:`dirname $0`/bin/*" play.core.server.NettyServer `dirname $0`"

nohup bash -c "${ICASA_COMMAND}  &"
