#!/usr/bin/env bash

docker build -t smolok/spark-standalone-worker:0.0.0-SNAPSHOT .
docker tag -f smolok/spark-standalone-worker:0.0.0-SNAPSHOT smolok/spark-standalone-worker:latest
docker push smolok/spark-standalone-worker:0.0.0-SNAPSHOT
docker push smolok/spark-standalone-worker:latest