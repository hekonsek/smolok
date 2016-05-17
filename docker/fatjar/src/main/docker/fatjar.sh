#!/bin/sh

# Optionally set XMX against JVM
if [ ! -z ${XMX} ]; then
    XMX="-Xmx${XMX}"
fi

if [ ! -z ${VERBOSE} ]; then
    echo "Executing command: java ${XMX} -jar /app/* $@"
fi
java ${XMX} -jar /app/* "$@"