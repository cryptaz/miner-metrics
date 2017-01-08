package org.cryptaz.minermetrics.models;

import org.cryptaz.minermetrics.models.dto.InfluxConfigDTO;

public class InfluxConfig {

    private String host;
    private String user;
    private String pass;
    private String db;

    public InfluxConfig(String host, String user, String pass, String db) {
        this.host = host;
        this.user = user;
        this.pass = pass;
        this.db = db;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getDb() {
        return db;
    }

    public void setDb(String db) {
        this.db = db;
    }

    public static InfluxConfigDTO convertToDTO(InfluxConfig influxConfig) {
        if(influxConfig == null){
            return null;
        }
        InfluxConfigDTO influxConfigDTO = new InfluxConfigDTO();
        influxConfigDTO.setHost(influxConfig.getHost());
        influxConfigDTO.setUser(influxConfig.getUser());
        influxConfigDTO.setPass(influxConfig.getPass());
        influxConfigDTO.setDb(influxConfig.getDb());
        return influxConfigDTO;
    }
    public static InfluxConfig convertFromDTO(InfluxConfigDTO influxConfigDTO) {
        if(influxConfigDTO == null){
            return null;
        }
        InfluxConfig influxConfig = new InfluxConfig(influxConfigDTO.getHost(), influxConfigDTO.getUser(),influxConfigDTO.getPass(),influxConfigDTO.getDb());
        return influxConfig;
    }
}
