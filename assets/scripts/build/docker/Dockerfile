FROM library/debian:latest
COPY deploy.sh /opt
COPY start.sh /opt
COPY influxd /etc/init.d
RUN rm /bin/sh && ln -s /bin/bash /bin/sh
RUN apt-get -y update && apt-get install -y git wget nano sudo curl apt-transport-https nginx
RUN /opt/deploy.sh
CMD /opt/start.sh