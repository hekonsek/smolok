#!/usr/bin/env bash

docker build -t smolok/spark-standalone-master:0.0.0-SNAPSHOT .
docker tag -f smolok/spark-standalone-master:0.0.0-SNAPSHOT smolok/spark-standalone-master:latest