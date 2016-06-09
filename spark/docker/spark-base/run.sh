#!/usr/bin/env bash

docker run --net=host --privileged -v ${HOME}/.smolok/spark/jobs:/root/.smolok/spark/jobs -it smolok/spark-submit:0.0.0-SNAPSHOT "$@"