package org.cryptaz.minermetrics.async;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.cryptaz.minermetrics.InfluxWriter;
import org.cryptaz.minermetrics.api.impl.ClaymoreAPI;
import org.cryptaz.minermetrics.models.ClaymoreInstanceInfo;
import org.cryptaz.minermetrics.models.DaemonStatus;
import org.cryptaz.minermetrics.models.dto.ClaymoreTickDTO;
import org.cryptaz.minermetrics.models.dto.TickDTO;
import org.joda.time.DateTime;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class AsyncTicker implements Runnable {

    private final static Logger log = org.apache.log4j.Logger.getLogger(AsyncTicker.class);


    private boolean isWorking = true;
    private InfluxWriter influxWriter;
    private long successfulTicks;
    private long failedTicks;
    private int tickTime;
    private int notificationTime;
    private String claymoreApiUrl;
    private Properties properties;
    private DateTime lastNotified;
    private List<ClaymoreAPI> claymores;

    public AsyncTicker(Properties properties) {
        this.properties = properties;
        this.tickTime = Integer.parseInt(properties.getProperty("tick_poll_time"));
        this.notificationTime = Integer.parseInt(properties.getProperty("stat_notification_time"));
        this.claymores = new ArrayList<>();
        String claymoreApiUrlBuffer = properties.getProperty("claymore_api_url");
        if (claymoreApiUrlBuffer.contains(";")) {
            String[] claymoresstring = claymoreApiUrlBuffer.split(";");
            log.info("Found multiple claymore instances (" + claymoresstring.length + ")");
            for (String claymoreUrl : claymoresstring) {
                log.info("Injecting claymore with endpoint at " + claymoreUrl);
                this.claymores.add(new ClaymoreAPI(claymoreUrl));
            }
        } else {
            log.info("Injecting claymore at " + claymoreApiUrlBuffer);
            this.claymores.add(new ClaymoreAPI(claymoreApiUrlBuffer));
        }
        influxWriter = new InfluxWriter(properties);
        this.lastNotified = null;
    }

    @Override
    public void run() {
        this.successfulTicks = 0;
        this.failedTicks = 0;
        log.info("Async worker started");
        while (isWorking) {
            DateTime dateTime = new DateTime();
            if (dateTime.getSecondOfMinute() % tickTime == 0) {
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
                    saveStatus(tickDTO);
                }
            }
            if (lastNotified == null || new DateTime().minusMinutes(notificationTime).isAfter(lastNotified)) {
                lastNotified = dateTime;
                log.info("Processed " + (successfulTicks + failedTicks) + " ticks [" + (successfulTicks + failedTicks) + " successful; " + failedTicks + " failed]");
            }
        }
    }

    private void saveStatus(TickDTO tickDTO) {
        try {
            DaemonStatus daemonStatus = new DaemonStatus();
            List<ClaymoreInstanceInfo> list = new ArrayList<>();
            for(ClaymoreAPI claymoreAPI : claymores) {
                ClaymoreInstanceInfo claymoreInstanceInfo = new ClaymoreInstanceInfo();
                claymoreInstanceInfo.setCardCount(tickDTO.getCardTickDatas().size());
                claymoreInstanceInfo.setUrl(claymoreAPI.getUrl());
                list.add(claymoreInstanceInfo);
            }
            daemonStatus.setClaymores(list);
            daemonStatus.setSuccessfulTicks(successfulTicks);
            daemonStatus.setFailedTicks(failedTicks);
            daemonStatus.setInitialized(true);
            daemonStatus.setStarted(true);
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(daemonStatus);
            File file = new File("status.json");
            FileUtils.writeStringToFile(file, json);

        } catch (IOException e) {
            e.printStackTrace();
            log.info("Could not save to file");
        }
    }
}