#!/bin/bash

## if exists a running pid then kill it
killIfExists() {
    if [ -f $1 ]; then
        kill -TERM $(cat $1)
    fi
}

cd $APP_DIR

killIfExists 'RUNNING_PID'

## run activator start
bin/$APP_NAME -Dhttp.port=$PORT