package org.cryptaz.minermetrics;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.cryptaz.minermetrics.async.AsyncTicker;
import org.cryptaz.minermetrics.models.Configuration;
import org.cryptaz.minermetrics.models.dto.ConfigurationDTO;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.File;
import java.io.IOException;

import static spark.Spark.get;
import static spark.Spark.port;

public class Application {

    private final static Logger log = Logger.getLogger(Application.class);

    public static void main(String[] args) throws IOException, InterruptedException {
        // Set up a simple configuration that logs on the console.
        log.info("MinerMetrics SNAPSHOT 1.0 started!");
        log.info("Loading configuration");
        final Configuration configuration = new Configuration();
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
            public String handle(Request request, Response response) throws Exception {
                log.debug("Request for configuration");
                ConfigurationDTO configurationDTO = Configuration.convertToDTO(configuration);
                String json =  new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(configurationDTO);
                return json;
            }
        };
        get("/configuration", reloadConfigurationEndpoint);

        log.info("Basic initializing passed, staring async worker.");

        AsyncTicker asyncTicker = new AsyncTicker(configuration);
        Thread thread = new Thread(asyncTicker);
        thread.start();
    }
}
