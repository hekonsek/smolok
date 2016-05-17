# Smolok

Smolok is a backend services platform for the Internet Of Things enablement. Smolok is based on the top of the leading
open source projects including [Eclipse IoT stack](http://iot.eclipse.org) and [Red Hat software](https://www.redhat.com/en).

The idea behind Smolok is to provide a scalable out-of-the-box PaaS platform providing core IoT services (like device
management, telemetry reading/writing, etc), big data analytics and IoT-scale messaging infrastructure.

## Getting started

In order to get started with Smolok you have to install Docker client on your computer. This is the only real
requirement to start playing with Smolok and IoT.

Smolok Cloud can be installed using Smolok command line tool. You can install command line too by executing the
following command:

    $ bash <(curl -sL https://goo.gl/vEyGhF)

Now when we have Smolok command line tool installed on our local machine, we can install and start Smolok Cloud:

    $ smolok cloud start
    Starting Smolok Cloud...
    Smolok Cloud started.

That's it! Smolok Cloud has been installed on your machine and is ready to handle IoT messages and to install additional
services into it.

## Documentation

Documentation of the project can be found [here](documentation.md).