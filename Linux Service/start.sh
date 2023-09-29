#!/bin/sh

cd /usr/local/devilspiderx

jarName=devilspiderx-*.jar
nohup java -jar $jarName >/dev/null 2>&1 &
DSXPid=$!
echo "DevilSpiderX Pid = $DSXPid"
echo $DSXPid >devilspiderx.pid
exit 0
