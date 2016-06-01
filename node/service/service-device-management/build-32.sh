#!/usr/bin/env bash

npm install
sudo docker build -t smolok/service-device-management-32:0.0.0-SNAPSHOT -f Dockerfile-32 .