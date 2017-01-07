package org.cryptaz.minermetrics.models;

public class DaemonStatus {

    private boolean initialized;
    private boolean started;
    private long successfulTicks;
    private long failedTicks;

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
}
