# Smolok

**Smolok - Big Data for connected devices**

Smolok is a big data platform for connected devices, where *device* can be a piece of the IoT (Internet Of Things) hardware,
smart phone, tablet, web browser or external IT system.

The idea behind Smolok is to deliver a scalable backend PaaS platform providing core services required by
device-oriented system. List of such services includes big data analytics, IoT-scale messaging infrastructure,
device management, telemetry reading/writing and so forth.

Smolok is based on the top of the leading open source projects including [Apache Spark](http://spark.apache.org) and
[Eclipse IoT stack](http://iot.eclipse.org)(particularly on the [Eclipse Kapua](https://projects.eclipse.org/proposals/eclipse-kapua) project).

## Getting started

[![Version](https://img.shields.io/badge/smolok-0.0.3-blue.svg)](https://github.com/smolok/smolok)

In order to get started with Smolok you have to install Docker client on your computer. This is the only real
requirement to start playing with Smolok and IoT.

Smolok Cloud can be installed using Smolok command line tool. You can install command line too by executing the
following command:

    $ bash <(curl -sL https://goo.gl/cLVVCF)

Now when we have Smolok command line tool installed on our local machine, we can install and start Smolok Cloud:

    $ smolok cloud start
    Starting Smolok Cloud...
    Smolok Cloud started.

That's it! Smolok Cloud has been installed on your machine and is ready to handle IoT messages and to install additional
services into it.

## Documentation

Documentation of the project can be found [here](documentation.md).
