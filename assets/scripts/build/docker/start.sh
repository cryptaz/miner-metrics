#!/bin/bash
function log {
    echo $1
    echo $1 >> /opt/start.sh.log
}

log "Adding metrics user"
useradd -m metrics
log "Starting Grafana"
service grafana-server start
log "Starting nginx"
service nginx start
mkdir /var/lib/influxd
chown -R influxdb:influxdb /var/lib/influxd
chmod +x /etc/init.d/influxd
log "Starting InfluxDB"
service influxd start
log "Preparing nginx (symlinks)"
rm /var/www/html/index.nginx-debian.html
touch /home/metrics/miner-metrics/target/default_dashboards.json
ln -s /home/metrics/miner-metrics/target/default_dashboards.json /var/www/html/index.html
ln -s /home/metrics/miner-metrics/target/daemon.log /var/www/html/daemon_log.html
ln -s /opt/start.sh.log /var/www/html/startup_log.html
log "Creating InfluxDB database"
influx -execute "create database minermetrics"
log "Deleting folder if exists"
rm -R /home/metrics/miner-metrics
log "Cloning from git"
sudo -u metrics git clone https://github.com/cryptaz/miner-metrics.git /home/metrics/miner-metrics
log "Compiling maven-project"
sudo -u metrics mvn -f /home/metrics/miner-metrics/pom.xml clean install
#FIXME add check for claymore api url is set
sudo -u metrics /bin/bash -c "cd /home/metrics/miner-metrics/target && java -jar minermetrics-1.0-SNAPSHOT-jar-with-dependencies.jar > miner-metrics.stdout 2>&1"
