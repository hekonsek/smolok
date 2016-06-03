FROM java:8u66

MAINTAINER Henryk Konsek <hekonsek@gmail.com>

ENV SPARK_DIST spark-1.5.1-bin-hadoop2.6
RUN curl -s http://d3kbcqa49mib13.cloudfront.net/${SPARK_DIST}.tgz | tar -xz -C /opt/
RUN cd /opt && ln -s ${SPARK_DIST} spark

ADD start-master.sh /start-master.sh
ADD scripts/start-worker.sh /start-worker.sh
ADD scripts/spark-submit.sh /spark-submit.sh
ADD scripts/spark-defaults.conf /spark-defaults.conf
ADD scripts/remove_alias.sh /remove_alias.sh

ENV SPARK_HOME /opt/spark

RUN cp $SPARK_HOME/conf/spark-env.sh.template $SPARK_HOME/conf/spark-env.sh 
RUN /bin/echo "export SPARK_WORKER_INSTANCES=2" >> $SPARK_HOME/conf/spark-env.sh
RUN ln -s $SPARK_HOME/sbin/start-slaves.sh /start-slaves.sh
RUN ln -s $SPARK_HOME/sbin/start-slave.sh /start-slave.sh
