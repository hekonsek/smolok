FROM java:8u91

MAINTAINER Henryk Konsek <hekonsek@gmail.com>

RUN curl -s http://ftp.piotrkosoft.net/pub/mirrors/ftp.apache.org/spark/spark-${spark.version}/spark-${spark.version}-bin-hadoop2.7.tgz | tar -xz -C /opt/
RUN cd /opt && ln -s spark-${spark.version}-bin-hadoop2.7 spark