#!/bin/bash

. /etc/profile
cd /usr/local/webserver
nohup java -Xms32M -Xmx512M --illegal-access=deny --add-opens=java.base/java.lang.invoke=ALL-UNNAMED -jar WebServer.jar >/dev/null 2>&1 &
echo $! >WebServer.pid
exit 0
