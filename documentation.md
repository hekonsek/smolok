# Smolok

Smolok is a backend services platform for the Internet Of Things enablement. Smolok is based on the top of the leading
open source projects including [Eclipse IoT stack](http://iot.eclipse.org) and [Red Hat software](https://www.redhat.com/en).

The idea behind Smolok is to provide a scalable out-of-the-box PaaS platform providing core IoT services (like device
management, telemetry reading/writing, etc), big data analytics and IoT-scale messaging infrastructure.

## Command line tool

Smolok command line tool can be used to create and manage Smolok platform from a command line. In order to install
Smolok command line tool, execute the following command:

    bash <(curl -sL https://goo.gl/vEyGhF)

The requirement for using Smolok command line tool is to have Docker client available in the path.

### Installing Raspbian on SD card

In order to install Raspbian Jessie (2016-02-26) to a SD card inserted into your laptop, use `smolok sdcard install-raspbian` command. For
example to install Raspbian to SD card device `/dev/mmcblk0`, execute the following command:

    smolok sdcard install-raspbian mmcblk0

Smolok will download a Raspbian image for you (if needed), extract it and install to the target SD card.

### Installing and starting Smolok Cloud

In order to start Smolok Cloud, just execute the following command:

    smolok cloud start

### Checking Smolok Cloud status

In order to check current status of Smolok Cloud, execute the following command:

    smolok cloud status

If Smolok Cloud is up and running you should see an output similar to the one below:

     $ smolok cloud status
     eventbus.canSend	true

If there are issues with some components of Smolok, it will be reported to the status command:

    $ smolok cloud status
    eventbus.canSend	false	Warning!

