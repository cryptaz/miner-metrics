# Miner Metrics 1.0-SNAPSHOT

Fancy miner metrics powered by Grafana and InfluxDB. Easy to setup and beatiful.

We need tests, so feel free to subimit issues.

Tool is highly experimental, I would say it's alpha version, so bugs may appear.

# Requirements
* Docker Toolbox (https://www.docker.com/products/docker-toolbox)
* Latest Claymore ZEC miner (eth miner should be supported also, but i did not tested it)

# Install
* Open Docker Quickstart terminal (or type natively if on linux) and type:

    ```
    docker pull cryptaz/miner-metrics
    docker run --name miner-metrics -d -p 80:3000 -p 8070:80 cryptaz/miner-metrics
    ```
* Patiently wait till environment is set up(10-30minutes)

# Usage
After docker container is started, type in terminal:
    ```
        docker ps
    ```
You will find out the ip of container. Just navigate to 192.168.99.100 to open Grafana dashboards. To load generated dashboards(instead of creating all by yourself), just open 192.168.99.100:8070, copy json, and import it normally from Grafana.
Default credentials for Grafana: admin:admin

# TODO
1) Log file
2) Test Claymore ETH dualminer
3) Tests
4) Add cryptocurrencies ticking and make profit dashboard