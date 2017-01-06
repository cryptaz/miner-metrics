#!/bin/bash
echo "Importing keys and setting up repositories"
echo "deb https://packagecloud.io/grafana/stable/debian/ jessie main" > /etc/apt/sources.list.d/grafana.list
echo "deb https://repos.influxdata.com/debian jessie stable" > /etc/apt/sources.list.d/influxdb.list
curl https://packagecloud.io/gpg.key | sudo apt-key add -
curl -sL https://repos.influxdata.com/influxdb.key | sudo apt-key add -
apt-get update
echo "Installing dependencies"
apt-get -y install default-jdk maven sudo apt-transport-https curl grafana influxdb