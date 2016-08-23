FROM smolok/spark-base:${project.version}

MAINTAINER Henryk Konsek <hekonsek@gmail.com>

ADD start-worker.sh /start-worker.sh
RUN chmod 775 /start-worker.sh

ENTRYPOINT ["/start-worker.sh"]