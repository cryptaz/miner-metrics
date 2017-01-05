package org.cryptaz.minermetrics;


import org.cryptaz.minermetrics.async.AsyncTicker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Application {

    private static Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws IOException {
        // Set up a simple configuration that logs on the console.
        logger.trace("Loading configuration");

        //Expect input claymore api url from docker environment
        //claymore_api_url=http://192.168.99.1:30500
        Set<Map.Entry<String,String>> environmentVariables = new HashSet<>();
        assert(environmentVariables.size() == 0);
        for(String arg : args) {
            //Maybe reorganize to maven envs (-Doption=value)
            if(arg.startsWith("claymore_api_url=")) {
                Map.Entry<String,String> var =
                        new AbstractMap.SimpleEntry<String, String>
                                ("claymore_api_url", arg.replaceAll("claymore_api_url=", ""));
                environmentVariables.add(var);
            }
            else {
                logger.warn("Unknown argument!");
            }
        }
        Configuration configuration;
        if(environmentVariables.size() > 0) {
             configuration = new Configuration("daemon.properties", environmentVariables);
        }
        else {
            configuration = new Configuration("daemon.properties", null);
        }
        if(args.length > 0) {
            logger.warn("The miner-metrics daemon does not expect to run with any arguments!");
        }
        logger.info("MinerMetrics SNAPSHOT 1.0 started!");
        logger.trace("Connecting to miners...");

        AsyncTicker asyncTicker = new AsyncTicker(configuration.getProperties());
        Thread thread = new Thread(asyncTicker);
        thread.start();
    }
}
