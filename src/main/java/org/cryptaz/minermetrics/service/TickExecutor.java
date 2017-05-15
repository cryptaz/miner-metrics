package org.cryptaz.minermetrics.service;

import org.cryptaz.minermetrics.api.impl.ClaymoreAPI;
import org.cryptaz.minermetrics.models.MinerEndpoint;
import org.cryptaz.minermetrics.models.dto.ClaymoreTickDTO;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
public class TickExecutor {

    private final static Logger log = LoggerFactory.getLogger(TickExecutor.class);

    private int successfulTicks;
    private int failedTicks;

    @Scheduled(fixedRate = 10000)
    public void tick() {
        log.debug("Tick is ticked");
        this.successfulTicks = 0;
        this.failedTicks = 0;
       /* DateTime dateTime = new DateTime();
            List<MinerEndpoint> minerEndpoints = configuration.getMinerEndpoints();
            if (minerEndpoints != null) {
                for (MinerEndpoint minerEndpoint : minerEndpoints) {
                    boolean found = false;
                    if (claymores != null) {
                        for (ClaymoreAPI claymore : claymores) {
                            if (Objects.equals(claymore.getUrl(), minerEndpoint.getUrl())) {
                                found = true;
                            }
                        }
                    }
                    if (!found || claymores == null) {
                        if (claymores == null) {
                            claymores = new ArrayList<>();
                        }
                        claymores.add(new ClaymoreAPI(minerEndpoint.getUrl()));
                    }
                }
            }

            if (claymores != null) {
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
        if (lastNotified == null || new DateTime().minusMinutes(notificationTime).isAfter(lastNotified)) {
            lastNotified = dateTime;
            if (claymores != null) {
                log.info("Processed " + (successfulTicks + failedTicks) + " ticks [" + (successfulTicks + failedTicks) + " successful; " + failedTicks + " failed]");
            } else {
                log.info("No miner endpoint specified! Configure it in Web UI");
            }
        }*/
    }
}
