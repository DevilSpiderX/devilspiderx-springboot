#!/bin/sh

cd /usr/local/webserver
nohup java --illegal-access=deny --add-opens=java.base/java.lang.invoke=ALL-UNNAMED -jar WebServer.jar >/dev/null 2>&1 &
javaPid=$!
echo "Java Pid = $javaPid"
echo $javaPid >WebServer.pid
exit 0
