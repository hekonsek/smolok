# Smolok

Smolok is a platform for the inter-device data stream communication, where *device* can be web browser, smart phone, tablet,
IoT (Internet Of Things) device or other IT system.

The idea behind Smolok is to deliver a scalable backend PaaS platform providing core services required by
devices. List of such services includes device management, telemetry reading/writing, big data analytics and IoT-scale
messaging infrastructure and so forth.

Smolok is based on the top of the leading open source projects including [Eclipse IoT stack](http://iot.eclipse.org)
(particularly on the [Eclipse Kapua](https://projects.eclipse.org/proposals/eclipse-kapua) project),
[Red Hat software stack](https://www.redhat.com/en) and [Apache Spark](http://spark.apache.org).

## Getting started

[![Version](https://img.shields.io/badge/smolok-0.0.0-blue.svg)](https://github.com/smolok/smolok)

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
