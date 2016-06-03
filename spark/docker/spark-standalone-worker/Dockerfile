FROM java:8u66

MAINTAINER Henryk Konsek <hekonsek@gmail.com>

RUN curl -s http://www-eu.apache.org/dist/spark/spark-1.6.1/spark-1.6.1-bin-hadoop2.6.tgz | tar -xz -C /opt/
RUN cd /opt && ln -s spark-1.6.1-bin-hadoop2.6 spark

ADD scripts/start-master.sh /start-master.sh
ADD scripts/start-worker.sh /start-worker.sh
ADD scripts/spark-submit.sh /spark-submit.sh
ADD scripts/spark-defaults.conf /spark-defaults.conf
ADD scripts/remove_alias.sh /remove_alias.sh

ENV SPARK_HOME /opt/spark

RUN ln -s $SPARK_HOME/sbin/start-slaves.sh /start-slaves.sh
RUN ln -s $SPARK_HOME/sbin/start-slave.sh /start-slave.sh

ENTRYPOINT ["/opt/spark/bin/spark-class", "org.apache.spark.deploy.worker.Worker", "spark://localhost:7077"]