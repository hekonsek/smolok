#!/usr/bin/env bash

cd /opt/spark
./bin/spark-class org.apache.spark.deploy.worker.Worker \
	spark://henryberg:7077 \
	--properties-file /spark-defaults.conf \
	-i $SPARK_LOCAL_IP \
	"$@"
