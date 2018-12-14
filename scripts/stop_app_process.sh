#!/bin/sh

PID="$(ps ax o pid,command | grep 'case-yellow' | grep -v grep | awk '{print $1}')"
echo $PID

kill -9 "${PID}"
exit 0