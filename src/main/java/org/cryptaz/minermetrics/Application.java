package org.cryptaz.minermetrics;


import org.cryptaz.minermetrics.async.AsyncTicker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Application {

    private static Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws IOException {
        // Set up a simple configuration that logs on the console.
        logger.trace("Loading configuration");

        Configuration configuration = new Configuration("daemon.properties");

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
