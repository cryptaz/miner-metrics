#!/bin/bash
echo "Adding metrics user"
useradd -m metrics
echo "Starting Grafana"
service grafana-server start
echo "Starting nginx"
service nginx start
mkdir /var/lib/influxd
chown -R influxdb:influxdb /var/lib/influxd
chmod +x /etc/init.d/influxd
echo "Starting InfluxDB"
service influxd start
echo "Creating InfluxDB database"
influx -execute "create database minermetrics"
echo "Deleting folder if exists"
rm -R /home/metrics/miner-metrics
echo "Cloning from git"
sudo -u metrics git clone https://github.com/cryptaz/miner-metrics.git /home/metrics/miner-metrics
sudo -u metrics mvn -f /home/metrics/miner-metrics/pom.xml clean install
rm /var/www/html/index.nginx-debian.html
touch /home/metrics/miner-metrics/target/default_dashboard.json
ln -s /home/metrics/miner-metrics/target/default_dashboard.json /var/www/html/index.html
ln -s /home/metrics/miner-metrics/target/daemon.log /var/www/html/daemon_log.html
#FIXME add check for claymore api url is set
sudo -u metrics /bin/bash -c "cd /home/metrics/miner-metrics/target && java -jar minermetrics-1.0-SNAPSHOT-jar-with-dependencies.jar > miner-metrics.stdout 2>&1"