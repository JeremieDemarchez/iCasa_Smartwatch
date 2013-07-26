#!/bin/bash

ICASA_COMMAND="play.core.server.NettyServer"
output=`ps aux | grep "${ICASA_COMMAND}"`
set -- ${output}
pid=$2
kill ${pid}
sleep 2
kill -9 ${pid} >/dev/null 2>&1