package org.cryptaz.minermetrics.models.dto;

public class NotificationDTO {

    private int successfulTicks;
    private int failedTicks;

    public int getSuccessfulTicks() {
        return successfulTicks;
    }

    public void setSuccessfulTicks(int successfulTicks) {
        this.successfulTicks = successfulTicks;
    }

    public int getFailedTicks() {
        return failedTicks;
    }

    public void setFailedTicks(int failedTicks) {
        this.failedTicks = failedTicks;
    }
}
