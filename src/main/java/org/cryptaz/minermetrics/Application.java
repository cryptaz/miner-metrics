package org.cryptaz.minermetrics;


import org.apache.log4j.Logger;
import org.cryptaz.minermetrics.async.AsyncTicker;
import org.cryptaz.minermetrics.models.Configuration;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static spark.Spark.port;
import static spark.Spark.post;

public class Application {

    private final static Logger log = Logger.getLogger(Application.class);

    public static void main(String[] args) throws IOException, InterruptedException {
        // Set up a simple configuration that logs on the console.
        log.info("MinerMetrics SNAPSHOT 1.0 started!");
        log.info("Loading configuration");
        Configuration configuration = new Configuration();
        try {
            configuration.loadFromFile(new File("config.json"));
        }
        catch (IOException e) {
            log.info("Could not load config. Creating new with default");
            try {
                configuration.initDefault();
            }
            catch (IOException e1) {
                log.info("Could not save default config. Maybe permissions? It was IOException");
            }
        }

        //Starting web-server. This is done for sending command to application
        port(9090);
        Route reloadConfigurationEndpoint = new Route() {
            @Override
            public Object handle(Request request, Response response) throws Exception {
                if(Objects.equals(request.body(), "")) {
                    response.status(400);
                    return "";
                }
                String json = request.body();
                log.info(json);
                return "";
            }
        };
        post("/reload/configuration", reloadConfigurationEndpoint);

        log.info("Basic initializing passed, staring async worker.");

        AsyncTicker asyncTicker = new AsyncTicker(configuration);
        Thread thread = new Thread(asyncTicker);
        thread.start();
    }
}
