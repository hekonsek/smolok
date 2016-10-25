# Smolok - Big Data for connected devices

[![Build](smolok.png)](https://github.com/smolok/smolok)

Smolok is a big data platform for connected devices, where *device* can be a piece of the IoT (Internet Of Things) hardware,
smart phone, tablet, web browser or external IT system.

The idea behind Smolok is to deliver a scalable backend PaaS platform providing core services required by
device-oriented system. List of such services includes big data analytics, IoT-scale messaging infrastructure,
device management, telemetry reading/writing and so forth.

Smolok is based on the top of the leading open source projects including [Apache Spark](http://spark.apache.org) and
[Eclipse IoT stack](http://iot.eclipse.org) (particularly on the [Eclipse Kapua](https://projects.eclipse.org/proposals/eclipse-kapua) project).

## High level overview

This section describes the high-level summary of the features provided by Smolok. Read this if you are interested in
learning what value can Smolok bring to you.

### Features summary

Smolok platform provides the following features enabling Big Data for connected devices:

- command line administration tool
- PaaS infrastructure
- messaging infrastructure
- application services

#### Command line administration tool

The primary administrative interface of Smolok is a command line tool which can be installed in less than a minute using
a single shell command. The command line administration tool provides the following features:

- simple Smolok installer
- single (and simple) administration interface
- easy access to platform status and monitoring information
- ability to add custom commands

#### Paas infrastructure

Smolok provisions Kubernetes-based PaaS backend that handles container-based deployment of the cloud services. In particular
PaaS provides:
- easy deployment of the services
- basic application nodes health-checks
- restarting crashed and stalled nodes
- services discovery
- services load balancing
- no-downtime deployments

#### Messaging infrastructure

Smolok provisions and manages messaging infrastructure that can be used for the following purposes:
- messaging backend for connected devices
- centralized AMQP-based event bus connecting devices and subsystems together
- queue system
- publish/subscribe notification infrastructure

#### Application services

Smolok provides out of the box backend services enabling IoT, Big Data and Machine Learning applications. For example:
- centralized configuration service
- device management service

### Common use cases

This section briefly describes common use cases of Smolok platform.

#### Install command line tool

Smolok command line tool can be used to create and manage Smolok platform from a command line. In order to install
Smolok command line tool, execute the following command:

    bash <(curl -sL https://goo.gl/fn110N)

In order to install Smolok, you should have Docker and Maven installed and present in your classpath. To install those
on Ubuntu execte the following command:

    curl -sSL https://get.docker.com/ | sh && apt-get install maven

To install Docker and Maven on CentOS 7.2 execute the following command:

    curl -sSL https://get.docker.com/ | sh && sudo systemctl enable docker.service && sudo systemctl start docker && \
      wget http://repos.fedorapeople.org/repos/dchen/apache-maven/epel-apache-maven.repo \
      -O /etc/yum.repos.d/epel-apache-maven.repo && \
      yum install apache-maven

In order to verify that command line tool has been properly installed, execute the following command:

    $ smolok --help
    Welcome to Smolok v0.0.6.

#### Starting Smolok Cloud

In order to start Smolok Cloud, just execute the following command:

    smolok cloud start

And you should see output similar to the following one:

    $ smolok cloud start
    Starting Smolok Cloud...
    Smolok Cloud started.

At this point Smolok Cloud is started. In particular Kubernetes server, AMQP-based event bus and configuration service are up and running.

#### Checking status of the Smolok cloud

So you have installed Smolok Cloud and you would like to check if everything went OK? Enter the `smolok cloud status`
command then:

    smolok cloud status

The status command displays information about current state of the Smolok Cloud. In particular `eventbus.canSend` metric
can tell you if the Smolok Event Bus is up and operational:

    $ smolok cloud status
    eventbus.canSend	true

#### Starting device management service

Now when you have your Smolok Cloud started, you probably would like to connect some device into it. Usually before you
connect any device into your event bus, you would like to register it into a device management service. In order to
start device management service, execute the following command:

    smolok service-start device

#### Starting REST protocol adapter

REST protocol adapter can be used to bridge HTTP requests to AMQP messages used by event bus i.e. access services
deployed into Smolok Cloud. In order to start REST protocol adapter execute the following command:

    smolok adapter-start rest

When REST protocol adapter is started you can access event bus using HTTP calls. For example to write a value `bar` into the
Configuration Service execute the REST call:

    curl http://YOUR-ADDRESS:8080/configuration/put/foo/bar

Now to read the same value execute the following HTTP request:

    $ curl http://YOUR-ADDRESS:8080/configuration/get/foo
    {"payload":"bar"}

## Command line tool

Smolok command line tool can be used to create and manage Smolok platform from a command line. In order to install
Smolok command line tool, execute the following command:

    bash <(curl -sL https://goo.gl/fn110N)

In order to install Smolok, you should have Docker and Maven installed and present in your classpath. To install those
on Ubuntu execte the following command:

    curl -sSL https://get.docker.com/ | sh && apt-get install maven

To install Docker and Maven on CentOS 7.2 execute the following command:

    curl -sSL https://get.docker.com/ | sh && sudo systemctl enable docker.service && sudo systemctl start docker && \
      wget http://repos.fedorapeople.org/repos/dchen/apache-maven/epel-apache-maven.repo \
      -O /etc/yum.repos.d/epel-apache-maven.repo && \
      yum install apache-maven

In order to verify that command line tool has been properly installed, execute the following command:

    $ smolok --help
    Welcome to Smolok v0.0.6.

### Enforcing Smolok version

You can tell Spark command line tool to use given Smolok version by setting `SMOLOK_VERSION` environment variable.
This option is particularly useful for development purposes. For example in order to tell Smolok to use version
`1.2.3-SNAPSHOT` execute the following command:

    SMOLOK_VERSION=1.2.3-SNAPSHOT smolok cloud start

## Smolok Cloud

Smolok Cloud is an actual backend platform used by Smolok to enable inter-devices communication. It is a set of backend
services connected together via AMQP-based event bus. Under the hood Smolok Cloud relies on Docker and
[Kubernetes](http://kubernetes.io).

### Starting Smolok Cloud

In order to start Smolok Cloud, just execute the following command:

    smolok cloud start

And you should see output similar to the following one:

    $ smolok cloud start
    Starting Smolok Cloud...
    Smolok Cloud started.

At this point Smolok Cloud is started. In particular Kubernetes server, AMQP-based event bus and configuration service are up and running.

### Checking Smolok Cloud status

In order to check current status of Smolok Cloud, execute the following command:

    smolok cloud status

If Smolok Cloud is up and running you should see an output similar to the one below:

     $ smolok cloud status
     eventbus.canSend	true

If there are issues with some components of Smolok, it will be reported to the status command:

    $ smolok cloud status
    eventbus.canSend	false	Warning!

### Services

Services are units of backend logic that can be deployed into Smolok Cloud in order to be accessed via event bus. Under
the hood services are deployed as Docker containers into Kubernetes cluster.

#### Configuration service

One of the services that comes with Smolok Cloud out of the box is Configuration Service. It is distributed key-value
service that can be read and modified using Event Bus.

Default Configuration Service started with Smolok Cloud is based Java properties file stored on the Kubernetes volume.

#### Device management service

When you have your Smolok Cloud started, you probably would like to connect some device into it. Usually before you
connect any device into your event bus, you would like to register it into a device management service. In order to
start device management service, execute the following command:

    smolok service-start device

### Protocol adapters

Protocol adapters can be used to bridge HTTP requests to AMQP messages used by event bus i.e. access services
deployed into Smolok Cloud.

#### Starting REST protocol adapter

REST protocol adapter can be used to bridge HTTP requests to AMQP messages used by event bus i.e. access services
deployed into Smolok Cloud. In order to start REST protocol adapter execute the following command:

    smolok adapter-start rest

When REST protocol adapter is started you can access event bus using HTTP calls. For example to write a value `bar` into the
Configuration Service execute the REST call:

    curl http://YOUR-ADDRESS:8080/configuration/put/foo/bar

Now to read the same value execute the following HTTP request:

    $ curl http://YOUR-ADDRESS:8080/configuration/get/foo
    {"payload":"bar"}

### Spark support

Smolok comes with a first-class citizen support for Apache Spark. Spark is usually used to analyze data sent through
Smolok and exchanged between devices.

#### Installing and starting standalone Apache Spark cluster

In order to install and start standalone Apache Spark cluster (dockerized Spark master and worker nodes connected
together) on your machine, execute the following command:

    smolok spark start

This command ensures that cluster is installed and properly started - components of the cluster that are already started
or installed, will be skipped from the process.

##### Installing and starting master/worker node only

You can also choose to start master or worker node only:

    smolok spark start master
    smolok spark start worker

If you would like to connect worker node to a certain Spark cluster, use `--master` option:

    smolok spark start worker --master=spark://mysparkmaster.com:7077

##### Setting up Spark node network

You can also use `--host` option to specify the hostname on which given Spark node should listen on
(default is `localhost`):

    smolok spark start master --host=myspark.com

The same option works for worker nodes as well:

    smolok spark start worker --master=spark://mysparkmaster.com:7077 --host=myworkernode.com

If you would like to specify master IP and local IP of the master node, use `--masterIP` and `--localIP` options
respectively:

    smolok spark start master --host=myspark.com --masterIP=192.168.1.1 --localIP=192.168.1.1

You can also use `localIP` option for worker node:

    smolok spark start worker --host=myspark.com --localIP=192.168.1.2

#### Submitting job into Spark cluster

In order to submit jar containing Spark job into the local Spark cluster, put your jar into `/var/smolok/spark/jobs`
directory and execute the following command:

    smolok spark submit --master=spark://localhost:7077 my-job.jar

For example to submit Smolok [RDD job archetype](https://github.com/smolok/smolok/tree/master/spark/archetypes/rdd)
to the local Spark cluster, copy `smolok-spark-archetypes-rdd-0.0.3-SNAPSHOT.jar` file into `/var/smolok/spark/jobs`
directory and execute the following command:

    smolok spark submit --master=spark://localhost:7077 smolok-spark-archetypes-rdd-0.0.3-SNAPSHOT.jar

You can also specify Spark `deploy-mode` when submitting the task:

    smolok spark submit --deploy-mode=cluster --master=spark://localhost:7077 smolok-spark-archetypes-rdd-0.0.3-SNAPSHOT.jar

Default value for `--master` in `client` deploy mode is `spark://localhost:7077` and for `cluster` deploy mode it is `spark://localhost:6066`

After job submission docker container is automatically removed, but this can be disabled using `--keep-logs` option.

## Zeppelin support

Smolok comes with support for Apache Zeppelin which integrates easily with Apache Spark cluster setup by Smolok.

### Installing Apache Zeppelin

To install Apache Zeppelin you just need to execute following command:

    smolok zeppelin start

This command ensures that application is installed and properly started - if application is already started
or installed, will skipped the process.

### Start options

 Command | Description | Default value
 --- | --- | ---
 --master | Spark master url | spark://localhost:6066 or spark://localhost:7077 depends on deploy-mode
 --localIP | Spark local IP |  |
 --port | Zeppelin http port | 8080 |
 --confDir | Zeppelin configuration directory | /opt/zeppelin/conf
 --notebookDir | Zeppelin notebook directory | /opt/zeppelin/notebook

Any other options will be passed to spark submit command eg. --deploy-mode cluster --driver-memory 2G etc.

### Persist notebooks locally

If you need to persist notebooks locally there is mounded volume `/var/smolok/zeppelin` so you can use if as follow:

    smolok zeppelin start --notebookDir=/var/smolok/zeppelin/notebook

This way notebooks will be stored in `/var/smolok/zeppelin/notebook` directory

### Start command example

    smolok zeppelin start --localIP=192.168.0.108 --port=8182 --deploy-mode client --executor-memory 2G

## Raspberry Pi support

Smolok comes with a dedicated support for Raspberry Pi 3 which makes it easier to connect IoT gateways based on this
board into Smolok.

### Installing Raspbian on SD card

In order to install Raspbian Jessie (2016-02-26) to a SD card inserted into your laptop, use `smolok sdcard install-raspbian` command. For
example to install Raspbian to SD card device `/dev/mmcblk0`, execute the following command:

    smolok sdcard install-raspbian mmcblk0

Smolok will download a Raspbian image for you (if needed), extract it and install to the target SD card.