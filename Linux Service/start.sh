#!/bin/sh

cd /usr/local/webserver

jarName=webserver-*.jar
nohup java --illegal-access=deny --add-opens=java.base/java.lang.invoke=ALL-UNNAMED -jar $jarName >/dev/null 2>&1 &
javaPid=$!
echo "Java Pid = $javaPid"
echo $javaPid >webserver.pid
exit 0
