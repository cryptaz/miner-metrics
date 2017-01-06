package org.cryptaz.minermetrics;


import org.apache.log4j.Logger;
import org.cryptaz.minermetrics.async.AsyncTicker;
import org.joda.time.DateTime;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Application {

    private final static Logger log = Logger.getLogger(Application.class);

    public static void main(String[] args) throws IOException, InterruptedException {
        // Set up a simple configuration that logs on the console.
        log.info("Loading configuration");

        //Expect input claymore api url from docker environment
        Set<Map.Entry<String,String>> environmentVariables = new HashSet<>();
        assert(environmentVariables.size() == 0);
        for(String arg : args) {
            //Maybe reorganize to maven envs (-Doption=value)
            if(arg.startsWith("claymore_api_url=")) {
                Map.Entry<String,String> var =
                        new AbstractMap.SimpleEntry<String, String>
                                ("claymore_api_url", arg.replaceAll("claymore_api_url=", ""));
                if(arg.split(";").length>1) {
                    log.info("Detected multiple claymore instances with endpoints:");
                    String[] urls = arg.split(";");
                    for(String endpoint: urls) {
                        log.info(endpoint);
                    }
                }
                environmentVariables.add(var);
            }
            else {
                log.warn("Unknown argument!");
            }
        }
        Configuration configuration;
        if(environmentVariables.size() > 0) {
             configuration = new Configuration("daemon.properties", environmentVariables);
        }
        else {
            while(true) {

                DateTime dateTime = new DateTime();
                if (dateTime.getSecondOfMinute() % 5 == 0) {
                    log.info("Docker container was called without required environment variables! Reinitialise container with proper values!");
                    Thread.sleep(1000);
                }
            }
        }
        log.info("MinerMetrics SNAPSHOT 1.0 started!");
        log.info("Connecting to miners...");

        AsyncTicker asyncTicker = new AsyncTicker(configuration.getProperties());
        Thread thread = new Thread(asyncTicker);
        thread.start();
    }
}
