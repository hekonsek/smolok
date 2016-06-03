#!/usr/bin/env bash

. ./build.sh
docker push smolok/spark-standalone-master:0.0.0-SNAPSHOT
docker push smolok/spark-standalone-master:latest