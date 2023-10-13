#!/bin/bash

pid=$(ps -ef | grep "microedge-agent-1.0-fat.jar" | grep -v grep | awk '{print $2}')
if [ -n "$pid" ]; then
        echo "Killing microedge agent pid $pid"
        kill -9 $pid
else
  echo "microedge agent is not running"
fi
