# Miner Metrics 1.0-SNAPSHOT

Fancy miner metrics powered by Grafana and InfluxDB. Easy to setup and beautiful.

We need tests, so feel free to submit issues.

Tool is highly experimental, I would say it's alpha version, so bugs may appear.

# Requirements
* Docker Toolbox (https://www.docker.com/products/docker-toolbox)
* Latest Claymore ZEC miner (eth miner should be supported also, but I did not tested it)

# Install
* Open Docker Quickstart terminal (or type natively if on linux) and type:

    ```
    docker pull cryptaz/miner-metrics
    ```
* Patiently wait until environment is set up. It may take up to 30 minutes.
* Start the container:

    ```
    docker run --name miner-metrics -e CLAYMORE_API_URL='YOUR_CLAYMORE_API_URL' -d -p 80:3000 -p 8070:80 cryptaz/miner-metrics
    ```

YOUR_CLAYMORE_API_URL should be url, that points to Claymore miner monitoring port(set up by -mport port).
URL should point on the docker's host interface(virtualbox host-only), so obtain it through ```ipconfig /all``` (or ```ip addr``` if on Linux)
Mine was 192.168.99.1, so my url looked like this ```http://192.168.99.1:30500```


# Usage
After docker container is started, type in terminal:
    ```
    docker ps
    ```
You will find out the ip of container. Just navigate to http://192.168.99.100 to open Grafana dashboards.
To load generated dashboards(instead of creating all by yourself), just open http://192.168.99.100:8070, copy json, and import it normally from Grafana.
Default credentials for Grafana: admin:admin

# Build
I have attached building scripts in my repo(assets/scripts/build/docker), so you can compile image by yourself.
Just clone repo, move into folder and type:
    ```
    docker build -t cryptaz/miner-metrics .
    ```
And then start normally as described above.

# TODO
* Log file (Severe)
* Test Claymore ETH dualminer
* Tests
* Add cryptocurrencies ticking and make profit dashboard
* Maybe add some sort of Admin UI for configuring some logic and miner-metrics-daemon itself, exporting json.


# Troubleshooting
Some places where to find out what's going on.
Firstly, ensure that container started:
    ```
    docker ps
    ```
You should see something like this:
```
```

If daemon did not start, check miner-metrics.stdout for any error messages(appears in your current directory).