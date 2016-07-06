#!/usr/bin/env bash

if [ -z "${HOST}" ]; then
  HOST='localhost'
fi

/opt/spark/sbin/start-master.sh -h ${HOST}
/bin/bash