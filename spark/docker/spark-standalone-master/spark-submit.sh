#!/usr/bin/env bash

SPARK_MASTER_SERVICE_HOST=`docker inspect spark_master | grep IPAddress\": | cut -d '"' -f 4`
docker run -e SPARK_MASTER_SERVICE_HOST=${SPARK_MASTER_SERVICE_HOST} --net=host -v /tmp/jobs:/tmp/jobs -it smolok/spark-standalone:0.0.0-SNAPSHOT /spark-submit.sh "$@"