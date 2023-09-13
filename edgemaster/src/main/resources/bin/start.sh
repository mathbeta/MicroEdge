#!/bin/bash
java_cmd=$(which java)
nohup $java_cmd -jar target/edgemaster-1.0.jar > master.log &
