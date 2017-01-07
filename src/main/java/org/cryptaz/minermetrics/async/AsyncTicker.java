package org.cryptaz.minermetrics.async;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.cryptaz.minermetrics.InfluxWriter;
import org.cryptaz.minermetrics.api.impl.ClaymoreAPI;
import org.cryptaz.minermetrics.models.Configuration;
import org.cryptaz.minermetrics.models.Constants;
import org.cryptaz.minermetrics.models.DaemonStatus;
import org.cryptaz.minermetrics.models.MinerEndpoint;
import org.cryptaz.minermetrics.models.dto.ClaymoreTickDTO;
import org.joda.time.DateTime;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.ArrayList;
import java.util.List;

import static spark.Spark.get;

public class AsyncTicker implements Runnable {

    private final static Logger log = org.apache.log4j.Logger.getLogger(AsyncTicker.class);


    private boolean isWorking = true;
    private InfluxWriter influxWriter;
    private long successfulTicks;
    private long failedTicks;
    private int tickTime;
    private int notificationTime;
    private String claymoreApiUrl;
    private Configuration configuration;
    private DateTime lastNotified;
    private List<ClaymoreAPI> claymores;

    public AsyncTicker(Configuration configuration) {
        this.configuration = configuration;
        this.tickTime = configuration.getTickTime();
        this.notificationTime = Constants.STAT_NOTIFICATION_TIME;
        if(configuration.getMinerEndpoints() == null) {
            log.info("No miners endpoints is configured. Configure it from WebUI");
            this.claymores = null;
        }
        else {
            log.info("Found " +  configuration.getMinerEndpoints().size() + " miners endpoints.");
            List<MinerEndpoint> minerEndpoints = configuration.getMinerEndpoints();
            List<ClaymoreAPI> claymores = new ArrayList<>();
            for(MinerEndpoint minerEndpoint: minerEndpoints) {
                log.info("Injecting claymore at endpoint: " + minerEndpoint.getUrl());
                ClaymoreAPI claymoreAPI = new ClaymoreAPI(minerEndpoint.getUrl());
                claymores.add(claymoreAPI);
            }
        }
        assert (configuration.getInfluxConfig()!= null);
        influxWriter = new InfluxWriter(configuration.getInfluxConfig());
        this.lastNotified = null;


        //FIXME Oh my god, why HERE? will fix it later
        Route getDaemonStatus = new Route("/status") {
            @Override
            public Object handle(Request request, Response response) {
                log.debug("Request for daemon status");
                DaemonStatus daemonStatus = new DaemonStatus();
                daemonStatus.setStarted(true);
                daemonStatus.setInitialized(true);
                daemonStatus.setSuccessfulTicks(successfulTicks);
                daemonStatus.setFailedTicks(failedTicks);
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(daemonStatus);
                } catch (JsonProcessingException e) {
                    response.status(500);
                    return "Could not turn status to json string";
                }
            }
        };
        get(getDaemonStatus);
    }

    @Override
    public void run() {
        this.successfulTicks = 0;
        this.failedTicks = 0;
        log.info("Async worker started");
        while (isWorking) {
            DateTime dateTime = new DateTime();
            if (dateTime.getSecondOfMinute() % tickTime == 0) {
                if(claymores != null) {
                    for (ClaymoreAPI claymoreAPI : claymores) {
                        ClaymoreTickDTO tickDTO = claymoreAPI.tick();
                        if (tickDTO == null) {
                            failedTicks++;
                            continue;
                        }
                        boolean wrote = influxWriter.writeClaymoreTick(tickDTO);
                        if (!wrote) {
                            failedTicks++;
                            continue;
                        }
                        successfulTicks++;
                    }
                }
            }
            if (lastNotified == null || new DateTime().minusMinutes(notificationTime).isAfter(lastNotified)) {
                lastNotified = dateTime;
                if(claymores != null) {
                    log.info("Processed " + (successfulTicks + failedTicks) + " ticks [" + (successfulTicks + failedTicks) + " successful; " + failedTicks + " failed]");
                }
                else {
                    log.info("No miner endpoint specified! Configure it in Web UI");
                }
            }
        }
    }
}