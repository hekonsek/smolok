#!/usr/bin/env bash

npm install
sudo docker build -t smolok/service-loader-32:0.0.0-SNAPSHOT -f Dockerfile-32 .
sudo docker tag -f smolok/service-loader-32:0.0.0-SNAPSHOT smolok/service-loader-32:latest
sudo docker push smolok/service-loader-32:0.0.0-SNAPSHOT
sudo docker push smolok/service-loader-32:latest