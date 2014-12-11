#!/bin/bash

## if exists a running pid then kill it
killIfExists() {
    if [ -f $1 ]; then
        kill -TERM $(cat $1)
    fi
}

cd $APP_DIR

killIfExists 'target/universal/stage/RUNNING_PID'
killIfExists 'RUNNING_PID'

## run activator start
export $OPTS

./activator start -Dhttp.port=$PORT