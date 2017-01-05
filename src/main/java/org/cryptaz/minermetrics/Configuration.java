package org.cryptaz.minermetrics;


import org.apache.log4j.Logger;

import java.io.*;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class Configuration {

    private final static org.apache.log4j.Logger log = Logger.getLogger(Application.class);

    private Properties properties;

    public Configuration() throws Exception {
        throw new Exception("Do not call without file");
    }

    public Configuration(String path, Set<Map.Entry<String,String>> environmentVariables) throws IOException {
        File file = new File(path);
        if(!file.exists()){
            log.info("No config file found! Creating default");
            initFile(path);
        }
        this.properties = new Properties();
        try {
            file = new File(path);
            InputStream inputStream = new FileInputStream(file);
            properties.load(inputStream);
        } catch (IOException | NullPointerException e) {
            log.error("Could not load configuration file (IOException). Exiting");
            System.exit(1);
        }

        if(environmentVariables != null) {
            for(Map.Entry<String, String> var : environmentVariables){
                properties.setProperty(var.getKey(), var.getValue());
            }
        }


    }

    private void initFile( String path) {
        Properties properties = new Properties();
        properties.setProperty("claymore_api_url", "http://192.168.99.1:30500");
        properties.setProperty("tick_poll_time", "5");
        properties.setProperty("stat_notification_time", "1");
        properties.setProperty("influxdb_host", "http://127.0.0.1:8086");
        properties.setProperty("influxdb_user", "root");
        properties.setProperty("influxdb_pass", "root");
        properties.setProperty("influxdb_db", "minermetrics");
        try {
            OutputStream outputStream = new FileOutputStream(new File(path));
            properties.store(outputStream, null);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e1) {
            log.error("Could initiate configuration file, check your permissions. Exiting");
            System.exit(1);
        }
    }

    public Properties getProperties() {
        return properties;
    }

}
