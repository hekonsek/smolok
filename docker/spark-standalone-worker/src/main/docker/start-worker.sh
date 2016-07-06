#!/usr/bin/env bash

if [ -z "${SPARK_MASTER}" ]; then
  SPARK_MASTER='spark://localhost:7077'
fi

if [ -z "${INTERFACE}" ]; then
  INTERFACE='localhost'
fi

/opt/spark/sbin/start-slave.sh ${SPARK_MASTER} -h ${INTERFACE}
/bin/bash