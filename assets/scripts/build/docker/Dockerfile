FROM library/debian:latest
COPY deploy.sh /opt
COPY influxd /etc/init.d
RUN rm /bin/sh && ln -s /bin/bash /bin/sh
RUN apt-get -y update && apt-get install -y git wget nano sudo curl apt-transport-https nginx
RUN /opt/deploy.sh
CMD sudo -u metrics /bin/bash -c "cd /home/metrics/miner-metrics/target && java -jar minermetrics-1.0-SNAPSHOT-jar-with-dependencies.jar > miner-metrics.stdout 2>&1"