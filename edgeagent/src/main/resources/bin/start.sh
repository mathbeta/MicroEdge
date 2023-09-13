#!/bin/bash
java_cmd=$(which java)
nohup $java_cmd -jar target/edgeagent-1.0-fat.jar > agent.log &
