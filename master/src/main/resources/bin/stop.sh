#!/bin/bash

pid=$(ps -ef | grep "microedge-master-1.0.jar" | grep -v grep | awk '{print $2}')
if [ -n "$pid" ]; then
	echo "Killing microedge master pid $pid"
	kill -9 $pid
else
  echo "microedge master is not running"
fi
