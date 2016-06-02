#!/usr/bin/env bash

sudo docker run --net=host --privileged -v /var/run/docker.sock:/run/docker.sock -it smolok/service-loader-32:0.0.0-SNAPSHOT