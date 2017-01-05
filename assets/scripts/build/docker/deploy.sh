echo "Importing keys and setting up repositories"
echo "deb https://packagecloud.io/grafana/stable/debian/ jessie main" > /etc/apt/sources.list.d/grafana.list
echo "deb https://repos.influxdata.com/debian jessie stable" > /etc/apt/sources.list.d/influxdb.list
curl https://packagecloud.io/gpg.key | sudo apt-key add -
curl -sL https://repos.influxdata.com/influxdb.key | sudo apt-key add -
apt-get update
echo "Installing dependencies"
apt-get -y install default-jdk maven sudo apt-transport-https curl grafana influxdb
update-rc.d grafana-server defaults
service grafana-server start
mkdir /var/lib/influxd
chown -R influxdb:influxdb /var/lib/influxd
chmod +x /etc/init.d/influxd
echo "Starting InfluxDB"
service influxd start
influx -execute "create database minermetrics"
useradd -m metrics
#FIXME add check for claymore api url is set
sudo -u metrics git clone https://github.com/cryptaz/miner-metrics.git /home/metrics/miner-metrics
sudo -u metrics mvn -f /home/metrics/miner-metrics/pom.xml clean install