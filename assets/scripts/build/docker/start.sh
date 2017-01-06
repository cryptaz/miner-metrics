#!/bin/bash
echo "" > /opt/start.sh.log

if [ ! -f /home/metrics/.initialized ]; then
    echo "Loading first time, initializing" >> /opt/start.sh.log
    echo "Adding metrics user" >> /opt/start.sh.log
    useradd -m metrics >> /opt/start.sh.log
    mkdir /var/lib/influxd >> /opt/start.sh.log
    chown -R influxdb:influxdb /var/lib/influxd >> /opt/start.sh.log
    chmod +x /etc/init.d/influxd >> /opt/start.sh.log
    echo "Preparing nginx (symlinks)" >> /opt/start.sh.log
    rm /var/www/html/index.nginx-debian.html >> /opt/start.sh.log
    echo "Miner Metrics not initialized yet!" > /var/www/html/index.html
    echo "Miner Metrics not initialized yet!" > /var/www/html/daemon_log.html
    ln -s /opt/start.sh.log /var/www/html/startup_log.html >> /opt/start.sh.log
    echo "Starting InfluxDB" >> /opt/start.sh.log
    service influxd start >> /opt/start.sh.log
    echo "Creating InfluxDB database" >> /opt/start.sh.log
    influx -execute "create database minermetrics" >> /opt/start.sh.log
else
    echo "Starting InfluxDB" >> /opt/start.sh.log
    service influxd start >> /opt/start.sh.log
fi
echo "Starting Grafana" >> /opt/start.sh.log
service grafana-server start >> /opt/start.sh.log
echo "Starting nginx" >> /opt/start.sh.log
service nginx start >> /opt/start.sh.log
echo "Deleting miner-metrics project folder if exits" >> /opt/start.sh.log
rm -R /home/metrics/miner-metrics >> /opt/start.sh.log
echo "Cloning from git" >> /opt/start.sh.log
sudo -u metrics git clone https://github.com/cryptaz/miner-metrics.git /home/metrics/miner-metrics >> /opt/start.sh.log
echo "Compiling maven-project" >> /opt/start.sh.log
sudo -u metrics mvn -f /home/metrics/miner-metrics/pom.xml clean install >> /opt/start.sh.log
if [ ! -f /home/metrics/.initialized ]; then
    rm /var/www/html/index.html >> /opt/start.sh.log
    rm /var/www/html/daemon_log.html >> /opt/start.sh.log
    ln -s /home/metrics/miner-metrics/target/daemon.log /var/www/html/daemon_log.html >> /opt/start.sh.log
    ln -s /home/metrics/miner-metrics/target/default_dashboards.json /var/www/html/index.html >> /opt/start.sh.log
    echo "1" > /home/metrics/.initialized
    chmod 777 /home/metrics/.initialized
    echo "Successfully initialized for first time!"
fi
#FIXME add check for claymore api url is set
echo "Successfully compiled project, starting daemon."
sudo -u metrics /bin/bash -c "cd /home/metrics/miner-metrics/target && java -jar minermetrics-1.0-SNAPSHOT-jar-with-dependencies.jar > miner-metrics.stdout 2>&1"