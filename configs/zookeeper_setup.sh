#!/bin/bash

bin/zkServer.sh start /conf/zoo.cfg
sleep 2
bin/zkCli.sh create /tinyurl quit
bin/zkServer.sh stop
sleep 1
bin/zkServer.sh start-foreground
