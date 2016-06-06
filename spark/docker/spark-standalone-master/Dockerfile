FROM java:8u66

MAINTAINER Henryk Konsek <hekonsek@gmail.com>

RUN curl -s http://www-eu.apache.org/dist/spark/spark-1.6.1/spark-1.6.1-bin-hadoop2.6.tgz | tar -xz -C /opt/
RUN cd /opt && ln -s spark-1.6.1-bin-hadoop2.6 spark

ADD start-master.sh /start-master.sh

ENTRYPOINT ["/start-master.sh"]