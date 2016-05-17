FROM java:8u72

MAINTAINER hekonsek@gmail.com

RUN curl -sSL https://get.docker.com/ | sh

RUN mkdir /app
ADD fatjar.sh /opt/
RUN chmod +x /opt/fatjar.sh

ENTRYPOINT ["/opt/fatjar.sh"]