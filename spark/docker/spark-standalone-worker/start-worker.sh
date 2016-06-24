#!/usr/bin/env bash

if [ -z "${SPARK_MASTER}" ]; then
  SPARK_MASTER='spark://localhost:7077'
fi

/opt/spark/sbin/start-slave.sh spark://localhost:7077
/bin/bash