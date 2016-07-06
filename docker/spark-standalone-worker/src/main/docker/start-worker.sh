#!/usr/bin/env bash

if [ -z "${SPARK_MASTER}" ]; then
  SPARK_MASTER='spark://localhost:7077'
fi

if [ -z "${HOST}" ]; then
  HOST='localhost'
fi

/opt/spark/sbin/start-slave.sh ${SPARK_MASTER} -h ${HOST}
/bin/bash