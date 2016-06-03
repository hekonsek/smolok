#!/usr/bin/env bash

export SPARK_LOCAL_IP=`awk 'NR==1 {print $1}' /etc/hosts`
cd /opt/spark
bin/spark-submit --master spark://${SPARK_MASTER_SERVICE_HOST}:7077 --deploy-mode cluster --conf spark.driver.host=${SPARK_LOCAL_IP} "$@"