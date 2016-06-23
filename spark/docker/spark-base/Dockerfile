FROM java:8u66

MAINTAINER Henryk Konsek <hekonsek@gmail.com>

RUN curl -s http://ftp.piotrkosoft.net/pub/mirrors/ftp.apache.org/spark/spark-2.0.0-preview/spark-2.0.0-preview-bin-hadoop2.7.tgz | tar -xz -C /opt/
RUN cd /opt && ln -s spark-2.0.0-preview-bin-hadoop2.7 spark