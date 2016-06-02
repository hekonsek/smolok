#!/usr/bin/env bash

npm install
sudo docker build -t smolok/service-loader-32:0.0.0-SNAPSHOT -f Dockerfile-32 .
docker tag -f smolok/service-loader-32:0.0.0-SNAPSHOT smolok/service-loader-32:latest
docker push smolok/service-loader-32:0.0.0-SNAPSHOT
docker push smolok/service-loader-32:latest