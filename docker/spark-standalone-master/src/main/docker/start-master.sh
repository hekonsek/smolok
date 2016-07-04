#!/usr/bin/env bash

if [ -z "${MASTER_INTERFACE}" ]; then
  MASTER_INTERFACE='localhost'
fi

/opt/spark/sbin/start-master.sh -i ${MASTER_INTERFACE}
/bin/bash