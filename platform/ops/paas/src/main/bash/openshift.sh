#!/usr/bin/env bash

if [ ! -e ~/.smolok/openshift/openshift ]; then
    curl -sL https://github.com/openshift/origin/releases/download/v1.3.0-rc1/openshift-origin-server-v1.3.0-rc1-ac0bb1bf6a629e0c262f04636b8cf2916b16098c-linux-64bit.tar.gz | tar -xz -C /tmp
    mv /tmp/openshift-origin-server* ~/.smolok/openshift
fi
sudo ~/.smolok/openshift/openshift start &
sleep 15
~/.smolok/openshift/oc login -u admin -p admin
~/.smolok/openshift/oc new-project smolok