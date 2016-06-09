#!/usr/bin/env bash

docker build -t smolok/spark-base:0.0.0-SNAPSHOT .
docker tag -f smolok/spark-base:0.0.0-SNAPSHOT smolok/spark-base:latest
docker push smolok/spark-base:0.0.0-SNAPSHOT
docker push smolok/spark-base:latest