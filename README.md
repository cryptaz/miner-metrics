# Miner Metrics 1.01-SNAPSHOT

Fancy miner metrics powered by Grafana and InfluxDB. Easy to setup and beautiful.

We need tests, so feel free to submit issues.

Tool is highly experimental, I would say it's alpha version, so bugs may appear.

![Dashboard](/assets/images/dashboard_sample.png?raw=true "Demo")

# Features
* Fancy metrics by Grafana
* Multiple Claymore instances supported(for multiple rigs)

# Requirements
* Docker Toolbox (https://www.docker.com/products/docker-toolbox)
* Latest Claymore ZEC miner (eth miner should be supported too, but I did not tested it)

# Install
* Open Docker Quickstart terminal, remember the ip, assigned to docker (usually, 192.168.99.100, so I will be using it below), and type:

    ```
docker pull cryptaz/miner-metrics
    ```
* Patiently wait until image is downloading. It may take up to 30 minutes.
* Run container for the first time to initialize all services:

    ```
docker run --name miner-metrics -d -p 80:3000 -p 8080:80 cryptaz/miner-metrics
    ```
* Open http://192.168.99.100 and login via admin:admin
* Open Data Sources
* Add new data source with name ```influx```, type ```InfluxDB```, url ```http://127.0.0.1:8086```, database ```minermetrics```, user ```root```, password ```root```
* Go to http://192.168.99.100:8080 and wait until all services is initialized
* Add your claymore miner at configuration page (point to accessible -mport address, for me, it was http://192.168.99.1:30500)
* Open main page and generate dashboard for your Claymore instance and copy JSON
* Return to Grafana (http://192.168.99.100), click Dashboards - import and paste that JSON into textarea, enter name, choose datasource influx and apply
* Congratulations! You set up the metrics. Don't forget to choose time range

# Usage

* To open metrics, simply open http://192.168.99.100
* To generate new dashboards(for example, if you added new card or pointed new claymore), manage it in http://192.168.99.100:8080

* To start container:

    ```
    docker start miner-metrics
    ```
* To stop container:

    ```
    docker stop miner-metrics
    ```
* To remove container(deletes all data):

    ```
    docker rm miner-metrics
    ```

# Build
I have attached building scripts in my repo(assets/scripts/build/docker), so you can compile image by yourself. I'm building and pushing image from this folder.
* Just clone repo, move into folder and build(assuming you are in root repository folder):<br />
    ```cd assets/scripts/build/docker/``` <br />
    ```docker build -t cryptaz/miner-metrics .```
And then start normally as described above.

# TODO
* Test Claymore ETH dualminer
* Unit tests
* [ARCHITECT] get rid out of status.json
* Add cryptocurrencies ticking and make profit dashboard
* Extend management interface (Web UI)


# Troubleshooting
Some places where to find out what's going on. Firstly, ensure that container started by typing
    ```
docker ps
    ```
You should see something like this:
```
$ docker ps
CONTAINER ID        IMAGE                   COMMAND                  CREATED                  STATUS              PORTS                                        NAMES
a1dd27929608        cryptaz/miner-metrics   "/bin/sh -c 'sudo -u "   Less than a second ago   Up 1 seconds        0.0.0.0:8080->80/tcp, 0.0.0.0:80->3000/tcp   miner-metrics
```


# Architect notes
* All main logs are just symlinked between processes (look at start script)
* Field card_id in InfluxDB is integer. Don't forget it, if you manually edit dashboards