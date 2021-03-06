#!/bin/bash
#Env variables
#TODO more variables
DAEMON_CONFIG_PATH=/opt/config.json

echo "" > /opt/start.sh.log
echo "Starting miner-metrics" >> /opt/start.sh.log 2>> /opt/start.sh.log
rm -R /var/www/html/* >> /opt/start.sh.log 2>> /opt/start.sh.log
if [ ! -f /home/metrics/.initialized ]; then
    echo "Loading first time, initializing" >> /opt/start.sh.log 2>> /opt/start.sh.log
    echo "Adding metrics user" >> /opt/start.sh.log 2>> /opt/start.sh.log
    useradd -m metrics >> /opt/start.sh.log 2>> /opt/start.sh.log
    mkdir /var/lib/influxd >> /opt/start.sh.log 2>> /opt/start.sh.log
    chown -R influxdb:influxdb /var/lib/influxd >> /opt/start.sh.log 2>> /opt/start.sh.log
    chmod +x /etc/init.d/influxd >> /opt/start.sh.log 2>> /opt/start.sh.log
    echo "Preparing nginx (symlinks)" >> /opt/start.sh.log 2>> /opt/start.sh.log
    echo "{\"initialized\": false, \"started\": false}" > /var/www/html/status.json 2>> /opt/start.sh.log
    echo "Starting InfluxDB" >> /opt/start.sh.log 2>> /opt/start.sh.log
    service influxd start >> /opt/start.sh.log 2>> /opt/start.sh.log
    echo "Creating InfluxDB database" >> /opt/start.sh.log 2>> /opt/start.sh.log
    influx -execute "create database minermetrics" >> /opt/start.sh.log 2>> /opt/start.sh.log
    #creating fresh config. Miner-metrics daemon will pick this instruction and create fresh
    echo "First time load, daemon config will be initialized with default values" >> /opt/start.sh.log 2>> /opt/start.sh.log
    echo "CREATE_DEFAULT_CONFIG" > "$DAEMON_CONFIG_PATH"
else
    echo "{\"initialized\": true, \"started\": false}" > /var/www/html/status.json 2>> /opt/start.sh.log
    echo "Starting InfluxDB" >> /opt/start.sh.log 2>> /opt/start.sh.log
    service influxd start >> /opt/start.sh.log 2>> /opt/start.sh.log
fi
echo "Starting Grafana" >> /opt/start.sh.log 2>> /opt/start.sh.log
service grafana-server start >> /opt/start.sh.log 2>> /opt/start.sh.log
if [ -f /etc/nginx/sites-enabled/default ]; then
    echo "Removing default nginx config" >> /opt/start.sh.log 2>> /opt/start.sh.log
    rm /etc/nginx/sites-enabled/default >> /opt/start.sh.log 2>> /opt/start.sh.log
fi
echo "Configuring nginx properly" >> /opt/start.sh.log 2>> /opt/start.sh.log
cp /opt/nginx.config /etc/nginx/sites-enabled/miner-metrics
echo "Starting nginx" >> /opt/start.sh.log 2>> /opt/start.sh.log
service nginx start >> /opt/start.sh.log 2>> /opt/start.sh.log
echo "Miner metrics have not started yet. Wait few seconds until project is cloned from git (it takes about 10-60 seconds depending on your internet connection)" > /var/www/html/index.html 2>> /opt/start.sh.log
echo "Deleting miner-metrics project folder if exits" >> /opt/start.sh.log 2>> /opt/start.sh.log
if [ -d /home/metrics/miner-metrics ]; then
    rm -R /home/metrics/miner-metrics >> /opt/start.sh.log 2>> /opt/start.sh.log
fi
echo "Cloning from git" >> /opt/start.sh.log 2>> /opt/start.sh.log
git clone https://github.com/cryptaz/miner-metrics.git /home/metrics/miner-metrics >> /opt/start.sh.log 2>> /opt/start.sh.log
echo "It seems like it's cloned from git. Making webapp..." >> /opt/start.sh.log 2>> /opt/start.sh.log
chown metrics:metrics -R /home/metrics/miner-metrics >> /opt/start.sh.log 2>> /opt/start.sh.log
rm /var/www/html/index.html >> /opt/start.sh.log 2>> /opt/start.sh.log
ln -s /home/metrics/miner-metrics/webapp/bower_components /var/www/html/bower_components >> /opt/start.sh.log 2>> /opt/start.sh.log
ln -s /home/metrics/miner-metrics/webapp/controllers /var/www/html/controllers >> /opt/start.sh.log 2>> /opt/start.sh.log
ln -s /home/metrics/miner-metrics/webapp/css /var/www/html/css >> /opt/start.sh.log 2>> /opt/start.sh.log
ln -s /home/metrics/miner-metrics/webapp/states /var/www/html/states >> /opt/start.sh.log 2>> /opt/start.sh.log
ln -s /home/metrics/miner-metrics/webapp/templates /var/www/html/templates >> /opt/start.sh.log 2>> /opt/start.sh.log
ln -s /home/metrics/miner-metrics/webapp/app.js /var/www/html/app.js >> /opt/start.sh.log 2>> /opt/start.sh.log
ln -s /home/metrics/miner-metrics/webapp/index.html /var/www/html/index.html >> /opt/start.sh.log 2>> /opt/start.sh.log
ln -s /opt/start.sh.log /var/www/html/startup_log.html >> /opt/start.sh.log 2>> /opt/start.sh.log
echo "Compiling maven-project" >> /opt/start.sh.log 2>> /opt/start.sh.log
sudo -u metrics mvn -f /home/metrics/miner-metrics/pom.xml clean install >> /opt/start.sh.log 2>> /opt/start.sh.log
if [ ! -f /home/metrics/.initialized ]; then
    echo "1" > /home/metrics/.initialized >> /opt/start.sh.log 2>> /opt/start.sh.log
    chmod 777 /home/metrics/.initialized >> /opt/start.sh.log 2>> /opt/start.sh.log
    ln -s /home/metrics/.initialized /var/www/html/ >> /opt/start.sh.log 2>> /opt/start.sh.log
    echo "Successfully initialized for first time!" >> /opt/start.sh.log 2>> /opt/start.sh.log
fi
ln -s /home/metrics/miner-metrics/target/daemon.log /var/www/html/daemon_log.html >> /opt/start.sh.log 2>> /opt/start.sh.log
#FIXME add check for claymore api url is set
echo "Successfully compiled project, starting daemon." >> /opt/start.sh.log 2>> /opt/start.sh.log
rm /var/www/html/status.json >> /opt/start.sh.log 2>> /opt/start.sh.log
ln -s /home/metrics/miner-metrics/target/status.json /var/www/html/status.json >> /opt/start.sh.log 2>> /opt/start.sh.log
#Magic line for exposing config to daemon
ln -s $DAEMON_CONFIG_PATH /home/metrics/miner-metrics/target/config.json
chown metrics:metrics "$DAEMON_CONFIG_PATH"
sudo -u metrics /bin/bash -c "cd /home/metrics/miner-metrics/target && java -jar minermetrics-1.01-SNAPSHOT-jar-with-dependencies.jar > miner-metrics.stdout 2>&1"