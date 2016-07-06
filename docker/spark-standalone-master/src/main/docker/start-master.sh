#!/usr/bin/env bash

if [ -z "${INTERFACE}" ]; then
  INTERFACE='localhost'
fi

/opt/spark/sbin/start-master.sh -h ${INTERFACE}
/bin/bash