#!/bin/bash

. /etc/profile
cd /usr/local/webserver
nohup java -Xms32M -Xmx512M -jar WebServer.jar >/dev/null 2>&1 &
echo $! > WebServer.pid
exit 0
