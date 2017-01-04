package org.cryptaz.minermetrics.models;

import org.joda.time.Period;

public class GeneralTickData extends TickData{

    private String version;

    private Period uptime;

    private int overallHashrate;

    private Long acceptedShares;

    private Long rejectedShares;

    private String poolUrl;

    private int cardCount;

    private String workerName;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Period getUptime() {
        return uptime;
    }

    public void setUptime(Period uptime) {
        this.uptime = uptime;
    }

    public int getOverallHashrate() {
        return overallHashrate;
    }

    public void setOverallHashrate(int overallHashrate) {
        this.overallHashrate = overallHashrate;
    }

    public Long getAcceptedShares() {
        return acceptedShares;
    }

    public void setAcceptedShares(Long acceptedShares) {
        this.acceptedShares = acceptedShares;
    }

    public Long getRejectedShares() {
        return rejectedShares;
    }

    public void setRejectedShares(Long rejectedShares) {
        this.rejectedShares = rejectedShares;
    }

    public String getPoolUrl() {
        return poolUrl;
    }

    public void setPoolUrl(String poolUrl) {
        this.poolUrl = poolUrl;
    }

    public int getCardCount() {
        return cardCount;
    }

    public void setCardCount(int cardCount) {
        this.cardCount = cardCount;
    }

}
