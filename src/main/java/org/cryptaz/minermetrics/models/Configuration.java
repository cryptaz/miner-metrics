package org.cryptaz.minermetrics.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.cryptaz.minermetrics.models.dto.ConfigurationDTO;
import org.cryptaz.minermetrics.models.dto.InfluxConfigDTO;
import org.cryptaz.minermetrics.models.dto.MinerEndpointDTO;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Configuration {

    private final static Logger log = Logger.getLogger(Configuration.class);

    private int tickTime;

    private final static int DEFAULT_TICK_TIME = 3;
    private final static String DEFAULT_INFLUX_HOST = "http://127.0.0.1:8086";
    private final static String DEFAULT_INFLUX_USER = "root";
    private final static String DEFAULT_INFLUX_PASS = "root";
    private final static String DEFAULT_INFLUX_DB = "minermetrics";

    private List<MinerEndpoint> minerEndpoints;
    private InfluxConfig influxConfig;

    public void loadFromFile(File file) throws IOException {
        String json = FileUtils.readFileToString(file);
        ObjectMapper objectMapper = new ObjectMapper();
        ConfigurationDTO configurationDTO = objectMapper.readValue(json, ConfigurationDTO.class);
        if(configurationDTO.getMinerEndpoints() != null) {
            List<MinerEndpoint> minerEndpoints = new ArrayList<>();
            for (MinerEndpointDTO minerEndpointDTO : configurationDTO.getMinerEndpoints()) {
                MinerEndpoint minerEndpoint = new MinerEndpoint();
                minerEndpoint.setUrl(minerEndpointDTO.getUrl());
                minerEndpoints.add(minerEndpoint);
            }
            this.minerEndpoints = minerEndpoints;
        }

        InfluxConfigDTO influxConfigDTO = configurationDTO.getInfluxConfigDTO();
        InfluxConfig influxConfig = new InfluxConfig(influxConfigDTO.getHost(),influxConfigDTO.getUser(),influxConfigDTO.getPass(),influxConfigDTO.getDb());

        this.influxConfig = influxConfig;
        setTickTime(configurationDTO.getTickTime());
    }

    public void initDefault() throws IOException {
        //Default influx db
        InfluxConfig influxConfig = new InfluxConfig(DEFAULT_INFLUX_HOST, DEFAULT_INFLUX_USER, DEFAULT_INFLUX_PASS, DEFAULT_INFLUX_DB);

        this.influxConfig = influxConfig;
        this.tickTime = DEFAULT_TICK_TIME;

        InfluxConfigDTO influxConfigDTO = new InfluxConfigDTO();
        influxConfigDTO.setHost(influxConfig.getHost());
        influxConfigDTO.setUser(influxConfig.getUser());
        influxConfigDTO.setPass(influxConfig.getPass());
        influxConfigDTO.setDb(influxConfig.getDb());

        ConfigurationDTO configurationDTO = new ConfigurationDTO();
        configurationDTO.setMinerEndpoints(convertToDTO(this.minerEndpoints));
        configurationDTO.setTickTime(DEFAULT_TICK_TIME);
        configurationDTO.setInfluxConfigDTO(influxConfigDTO);


        ObjectMapper objectMapper = new ObjectMapper();
        String json = null;
        try {
            json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(configurationDTO);
        } catch (JsonProcessingException e) {
            log.info("Lol, probably you fucked up configuration model. I's clearly impossible to catch this line");
        }
        assert (json != null);
        FileUtils.writeStringToFile(new File("config.json"), json);
    }

    public void save() throws IOException {
        ConfigurationDTO configurationDTO = new ConfigurationDTO();
        configurationDTO.setTickTime(this.tickTime);
        if (this.minerEndpoints != null) {
            List<MinerEndpointDTO> minerEndpointDTOs = convertToDTO(this.minerEndpoints);
            configurationDTO.setMinerEndpoints(minerEndpointDTOs);
        }
        String json = null;
        json = new ObjectMapper().writeValueAsString(configurationDTO);
        FileUtils.writeStringToFile(new File("config.json"), json);
    }

    public List<MinerEndpoint> getMinerEndpoints() {
        return minerEndpoints;
    }

    public void setMinerEndpoints(List<MinerEndpoint> minerEndpoints) {
        this.minerEndpoints = minerEndpoints;
    }

    public int getTickTime() {
        return tickTime;
    }

    public void setTickTime(int tickTime) {
        this.tickTime = tickTime;
    }

    public InfluxConfig getInfluxConfig() {
        return this.influxConfig;
    }

    public List<MinerEndpointDTO> convertToDTO(List<MinerEndpoint> minerEndpoints) {
        if(minerEndpoints == null) {
            return null;
        }
        List<MinerEndpointDTO> minerEndpointDTOs = new ArrayList<>();
        for (MinerEndpoint minerEndpoint : minerEndpoints) {
            MinerEndpointDTO minerEndpointDTO = new MinerEndpointDTO();
            minerEndpointDTO.setUrl(minerEndpoint.getUrl());
            minerEndpointDTOs.add(minerEndpointDTO);
        }
        return minerEndpointDTOs;
    }
    public List<MinerEndpoint> convertFromDTO(List<MinerEndpointDTO> minerEndpointDTOs) {
        if(minerEndpointDTOs == null) {
            return  null;
        }
        List<MinerEndpoint> minerEndpoints = new ArrayList<>();
        for (MinerEndpointDTO minerEndpointDTO: minerEndpointDTOs) {
            MinerEndpoint minerEndpoint = new MinerEndpoint();
            minerEndpoint.setUrl(minerEndpointDTO.getUrl());
            minerEndpoints.add(minerEndpoint);
        }
        return minerEndpoints;
    }
}
