FROM library/debian:latest
COPY deploy.sh /opt
COPY start.sh /opt
COPY influxd /etc/init.d
COPY nginx.config /opt
RUN rm /bin/sh && ln -s /bin/bash /bin/sh
RUN apt-get -y update && apt-get install -y apt-transport-https nginx
RUN /opt/deploy.sh
CMD /opt/start.sh