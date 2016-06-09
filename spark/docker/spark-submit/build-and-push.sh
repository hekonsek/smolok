#!/usr/bin/env bash

docker build -t smolok/spark-submit:0.0.0-SNAPSHOT .
docker tag -f smolok/spark-submit:0.0.0-SNAPSHOT smolok/spark-submit:latest
docker push smolok/spark-submit:0.0.0-SNAPSHOT
docker push smolok/spark-submit:latest