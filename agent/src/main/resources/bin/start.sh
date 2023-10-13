#!/bin/bash
java_exec=$(which java)
nohup $java_exec -jar microedge-agent-1.0-fat.jar > agent.log &
