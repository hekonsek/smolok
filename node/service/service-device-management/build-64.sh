#!/usr/bin/env bash

npm install
sudo docker build -t smolok/service-device-management-64:0.0.0-SNAPSHOT -f Dockerfile-64 .