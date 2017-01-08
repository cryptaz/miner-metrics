package org.cryptaz.minermetrics.models;

import java.util.List;

public class DaemonStatus {

    private boolean initialized;
    private boolean started;
    private long successfulTicks;
    private long failedTicks;

    List<ClaymoreInstanceInfo> claymoreInstances;

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

    public List<ClaymoreInstanceInfo> getClaymoreInstances() {
        return claymoreInstances;
    }

    public void setClaymoreInstances(List<ClaymoreInstanceInfo> claymoreInstances) {
        this.claymoreInstances = claymoreInstances;
    }
}
