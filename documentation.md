# Smolok

Smolok is a platform for the inter-device data stream communication, where *device* can be web browser, smart phone, tablet,
IoT (Internet Of Things) device or other IT system.

The idea behind Smolok is to deliver a scalable backend PaaS platform providing core services required by
devices. List of such services includes device management, telemetry reading/writing, big data analytics and IoT-scale
messaging infrastructure and so forth.

Smolok is based on the top of the leading open source projects including [Eclipse IoT stack](http://iot.eclipse.org),
[Red Hat software](https://www.redhat.com/en) and [Apache Spark](http://spark.apache.org).

## Command line tool

Smolok command line tool can be used to create and manage Smolok platform from a command line. In order to install
Smolok command line tool, execute the following command:

    bash <(curl -sL https://goo.gl/vEyGhF)

The requirement for using Smolok command line tool is to have Docker client available in the path.

## Smolok Cloud

Smolok Cloud is an actual backend platform used by Smolok to enable inter-devices communication. It is a set of backend
services connected together via AMQP-based event bus. Under the hood Smolok Cloud relies on Docker and
[Kubernetes](http://kubernetes.io).

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

## Spark support

### Installing and starting standalone Apache Spark cluster

In order to install and start standalone Apache Spark cluster (dockerized Spark master and worker nodes connected
together) on your machine, execute the following command:

    smolok spark start

This command ensures that cluster is installed and properly started - components of the cluster that are already started
or installed, will be skipped from the process.

You can also choose to start master or worker node only:

    smolok spark start master
    smolok spark start worker

If you would like to connect worker node to a certain Spark cluster, use `--master` option:

    smolok spark start worker --master=spark://mysparkmaster.com:7077

### Submitting job into Spark cluster

In order to submit jar containing Spark job into the local Spark cluster, put your jar into `/var/smolok/spark/jobs`
directory and execute the following command:

    smolok spark submit --master=spark://localhost:7077 my-job.jar

## Raspberry Pi support

Smolok comes with a dedicated support for Raspberry Pi 3 which makes it easier to connect IoT gateways based on this
board into Smolok.

### Installing Raspbian on SD card

In order to install Raspbian Jessie (2016-02-26) to a SD card inserted into your laptop, use `smolok sdcard install-raspbian` command. For
example to install Raspbian to SD card device `/dev/mmcblk0`, execute the following command:

    smolok sdcard install-raspbian mmcblk0

Smolok will download a Raspbian image for you (if needed), extract it and install to the target SD card.