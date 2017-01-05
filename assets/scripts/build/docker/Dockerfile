FROM library/debian:latest
COPY deploy.sh /opt
COPY influxd /etc/init.d
RUN rm /bin/sh && ln -s /bin/bash /bin/sh
RUN apt-get -y update && apt-get install -y git wget nano sudo curl apt-transport-https nginx
RUN /opt/deploy.sh
CMD sudo -u metrics java -jar /home/metrics/miner-metrics/target/minermetrics-1.0-SNAPSHOT-jar-with-dependencies.jar claymore_api_url=$CLAYMORE_API_URL> /home/metrics/miner-metrics/miner-metrics.stdout 2>&1