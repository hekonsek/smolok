FROM smolok/spark-base:${project.version}

MAINTAINER Henryk Konsek <hekonsek@gmail.com>

ADD submit.sh /submit.sh
RUN chmod 775 /submit.sh

ENTRYPOINT ["/submit.sh"]