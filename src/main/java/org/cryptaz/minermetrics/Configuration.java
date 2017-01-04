package org.cryptaz.minermetrics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
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
            inputStream = new FileInputStream(new File(path));
            properties.load(inputStream);
        } catch (IOException e) {
            logger.warn("Configuration file not found(IOException), creating.");
            initFile(properties, path);
            inputStream = new FileInputStream(new File(path));
            properties.load(inputStream);
        }
    }

    private void initFile(Properties properties, String path) {
        try {
            OutputStream outputStream = new FileOutputStream(new File(path));
            setDefaultProperties();
            properties.store(outputStream, null);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e1) {
            logger.error("Could not neither load or save configuration file! Exiting");
            System.exit(1);
        }
    }

    private void setDefaultProperties() {
        properties.setProperty("url", "http://127.0.0.1:30300");
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }
}
