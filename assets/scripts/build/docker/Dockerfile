FROM library/debian:latest
COPY deploy.sh /opt
COPY influxd /etc/init.d
RUN rm /bin/sh && ln -s /bin/bash /bin/sh
RUN apt-get -y update && apt-get install -y git wget nano sudo curl apt-transport-https nginx
RUN /opt/deploy.sh
CMD docker run -e CLAYMORE_API_URL='http://192.168.99.1:30500' -d -p 80:3000 -p 8070:80 cryptaz/miner-metrics sudo -u metrics /bin/bash -c "cd /home/metrics/miner-metrics/target && java -jar minermetrics-1.0-SNAPSHOT-jar-with-dependencies .jar > miner-metrics.stdout 2>&1