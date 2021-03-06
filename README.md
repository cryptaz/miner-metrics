# Miner Metrics 1.01-SNAPSHOT

Fancy miner metrics powered by Grafana and InfluxDB. Easy to setup and beautiful.

We need tests, so feel free to submit issues.

Tool is highly experimental, I would say it's alpha version, so bugs may appear. By the way, if you have questions, according this project, you can ask me in Telegram @CryptoFan. I'm always happy to resolve any problems, also can consult you about cryptocurrencies.

![Dashboard](/assets/images/dashboard_sample.png?raw=true "Demo")

# Features
* Fancy metrics by Grafana
* Multiple Claymore instances supported(for multiple rigs)

# Requirements
* Docker Toolbox (https://www.docker.com/products/docker-toolbox)
* Latest Claymore ZEC miner (Claymore ETH miner supported too, but I did not tested it deeply)

# Install
* [LINUX] You should obtain your host docker ip address by yourself. When you fully pull and start image, try using ```docker-machine ip``` or ```docker inspect -f '{{ .NetworkSettings.IPAddress }}' miner-metrics``` to get ip address. Or you can simply watch over all of your interfaces by using ```ip addr```, just pick the right one.
* [WINDOWS] Open Docker Quickstart terminal, remember the ip, assigned to docker (usually, 192.168.99.100, so I will be using it below)
* Pull the image from Docker Hub repository:
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
Steps below are only can be appended to major version releases(when I change version number), all minor fixes applied, when you restart you container.
I have attached building scripts in my repo(assets/scripts/build/docker), so you can compile image by yourself. I'm building and pushing image from this folder.
* Just clone repo, move into folder and build(assuming you are in root repository folder):<br />
    ```cd assets/scripts/build/docker/``` <br />
    ```docker build -t cryptaz/miner-metrics .```
And then start normally as described above.

# TODO
* Unit tests
* [ARCHITECT] get rid out of status.json
* Add cryptocurrencies ticking and make profit dashboard
* Automated upgrade

# Upgrade
I will push new image to docker each new snapshot release. Just pull new image from docker using <br>
```docker pull cryptaz/miner-metrics```
<br>
However, at now, you will lose your data if you start new updated image(as you creating whole new machine).
Best way to migrate database, is to dump InfluxDB manually, upgrade, and then import data again (if you know how to do it). I will look into that soon to provide automated upgrade. It is on my todo list.
Alternatively, to avoid using data, you can download Influxdb for your host system, and point daemon to this database.
On each new snapshot, I will push new image to the docker repository.
However again, In general, it is not needede to upgrade docker, I am pushing minor modifications(which is not affecting docker envrionment) to Github master branch, so you can simply restart your container  to catch up modifications using 
```docker stop miner-metrics && docker start miner-metrics```

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