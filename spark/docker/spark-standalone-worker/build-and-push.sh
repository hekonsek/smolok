#!/usr/bin/env bash

. ./build.sh
docker push smolok/spark-standalone-worker:0.0.0-SNAPSHOT
docker push smolok/spark-standalone-worker:latest