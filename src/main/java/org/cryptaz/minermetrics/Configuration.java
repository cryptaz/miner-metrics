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

        File file = new File(path);
        if(!file.exists()){
            logger.info("No config file found! Creating default");
            initFile(path);
        }
        this.properties = new Properties();
        try {
            file = new File(path);
            InputStream inputStream = new FileInputStream(file);
            properties.load(inputStream);
        } catch (IOException | NullPointerException e) {
            logger.error("Could not load configuration file (IOException). Exiting");
            System.exit(1);
        }

    }


    private void initFile( String path) {
        Properties properties = new Properties();
        properties.setProperty("claymore_api_url", "http://192.168.99.1:30500");
        properties.setProperty("tick_poll_time", "5");
        properties.setProperty("stat_notification_time", "1");
        try {
            OutputStream outputStream = new FileOutputStream(new File(path));
            properties.store(outputStream, null);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e1) {
            logger.error("Could initiate configuration file, check your permissions. Exiting");
            System.exit(1);
        }
    }

    public Properties getProperties() {
        return properties;
    }

}
