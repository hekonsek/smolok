FROM java:8u91

MAINTAINER Henryk Konsek <hekonsek@gmail.com>

RUN curl -s http://ftp.ps.pl/pub/apache/zeppelin/zeppelin-0.6.1/zeppelin-0.6.1-bin-all.tgz | tar -xz -C /opt/
RUN cd /opt && ln -s zeppelin-0.6.1-bin-all zeppelin

CMD ["/opt/zeppelin/bin/zeppelin.sh"]