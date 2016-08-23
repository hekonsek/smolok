FROM smolok/spark-base:${project.version}

MAINTAINER Henryk Konsek <hekonsek@gmail.com>

ADD start-master.sh /start-master.sh
RUN chmod 775 /start-master.sh

ENTRYPOINT ["/start-master.sh"]