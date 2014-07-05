#!/bin/bash

cd $1

## if exists a running pid then kill it
if [ -f RUNNING_PID ]; then
    sudo kill -TERM $(cat RUNNING_PID)
fi

## run activator start
./activator start -Dhttp.port=$2

