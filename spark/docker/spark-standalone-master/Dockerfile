FROM java:8u66

MAINTAINER Henryk Konsek <hekonsek@gmail.com>

RUN curl -s http://www-eu.apache.org/dist/spark/spark-1.6.1/spark-1.6.1-bin-hadoop2.6.tgz | tar -xz -C /opt/
RUN cd /opt && ln -s spark-1.6.1-bin-hadoop2.6 spark

ADD start-master.sh /start-master.sh

ENV SPARK_HOME /opt/spark

RUN cp $SPARK_HOME/conf/spark-env.sh.template $SPARK_HOME/conf/spark-env.sh 
RUN /bin/echo "export SPARK_WORKER_INSTANCES=2" >> $SPARK_HOME/conf/spark-env.sh
RUN ln -s $SPARK_HOME/sbin/start-slaves.sh /start-slaves.sh
RUN ln -s $SPARK_HOME/sbin/start-slave.sh /start-slave.sh
