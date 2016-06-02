#!/usr/bin/env bash

npm install
sudo docker build -t smolok/service-device-management:0.0.0-SNAPSHOT .
sudo docker tag -f smolok/service-device-management:0.0.0-SNAPSHOT smolok/service-device-management:latest
sudo docker push smolok/service-device-management:0.0.0-SNAPSHOT
sudo docker push smolok/service-device-management:latest