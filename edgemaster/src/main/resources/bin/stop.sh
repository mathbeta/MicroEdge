#!/bin/bash

pid=$(ps -ef | grep "edgemaster-1.0.jar" | grep -v grep | awk '{print $2}')
if [ -n "$pid" ]; then
	echo "Killing edgemaster pid $pid"
	kill -9 $pid
fi
