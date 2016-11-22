FROM ubuntu

MAINTAINER Henryk Konsek <hekonsek@gmail.com>

RUN apt-get update
RUN apt-get install -qq autossh

ADD autossh.sh /opt/
RUN chmod +x /opt/autossh.sh

ENTRYPOINT ["/opt/autossh.sh"]