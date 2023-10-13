#!/bin/bash
java_exec=$(which java)
nohup $java_exec -jar microedge-master-1.0.jar > master.log &
