package org.cryptaz.minermetrics.models;

import java.util.List;

public class DaemonStatus {

    private boolean initialized;
    private boolean started;
    private long successfulTicks;
    private long failedTicks;
    private List<ClaymoreInstanceInfo> claymores;

    public long getSuccessfulTicks() {
        return successfulTicks;
    }

    public void setSuccessfulTicks(long successfulTicks) {
        this.successfulTicks = successfulTicks;
    }

    public long getFailedTicks() {
        return failedTicks;
    }

    public void setFailedTicks(long failedTicks) {
        this.failedTicks = failedTicks;
    }

    public List<ClaymoreInstanceInfo> getClaymores() {
        return claymores;
    }

    public void setClaymores(List<ClaymoreInstanceInfo> claymores) {
        this.claymores = claymores;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }
}
