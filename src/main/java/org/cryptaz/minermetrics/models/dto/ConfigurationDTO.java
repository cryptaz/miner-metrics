package org.cryptaz.minermetrics.models.dto;

import java.util.List;

public class ConfigurationDTO {

    private List<MinerEndpointDTO> minerEndpoints;

    private int tickTime;

    private InfluxConfigDTO influxConfigDTO;

    public List<MinerEndpointDTO> getMinerEndpoints() {
        return minerEndpoints;
    }

    public void setMinerEndpoints(List<MinerEndpointDTO> minerEndpoints) {
        this.minerEndpoints = minerEndpoints;
    }

    public int getTickTime() {
        return tickTime;
    }

    public void setTickTime(int tickTime) {
        this.tickTime = tickTime;
    }

    public InfluxConfigDTO getInfluxConfigDTO() {
        return influxConfigDTO;
    }

    public void setInfluxConfigDTO(InfluxConfigDTO influxConfigDTO) {
        this.influxConfigDTO = influxConfigDTO;
    }
}
