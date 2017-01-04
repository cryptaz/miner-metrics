package org.cryptaz.minermetrics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {

    private final Logger logger = LoggerFactory.getLogger(Configuration.class);
    private Properties properties;

    public Configuration() throws Exception {
        throw new Exception("Do not call without file");
    }

    public Configuration(String path) throws IOException {

        this.properties = new Properties();
        InputStream inputStream;
        try {
            properties.load(this.getClass().getClassLoader().getResourceAsStream(path));
        } catch (IOException | NullPointerException e) {
            logger.error("Could not load configuration file (IOException). Exiting");
            System.exit(1);
        }

    }

    public Properties getProperties() {
        return properties;
    }

}
