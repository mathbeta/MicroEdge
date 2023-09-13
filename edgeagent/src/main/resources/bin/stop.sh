#!/bin/bash

pid=$(ps -ef | grep "edgeagent-1.0-fat.jar" | grep -v grep | awk '{print $2}')
if [ -n "$pid" ]; then
        echo "Killing edgemaster pid $pid"
        kill -9 $pid
fi
