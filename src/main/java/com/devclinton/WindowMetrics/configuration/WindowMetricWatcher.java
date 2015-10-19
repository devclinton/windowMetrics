package com.devclinton.WindowMetrics.configuration;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Created by Clinton Collins <clinton.collins@gmail.com> on 10/15/15.
 */


@JsonTypeName("windowMetricWatcher")
public class WindowMetricWatcher {
    private int interval = 10;
    private int idleAfter = 60;
    private boolean logIdleTitle = false;

    private boolean checksum = true;
    private int checksumTimeout = 60 * 60 * 48;

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getIdleAfter() {
        return idleAfter;
    }

    public void setIdleAfter(int idleAfter) {
        this.idleAfter = idleAfter;
    }

    public boolean isLogIdleTitle() {
        return logIdleTitle;
    }

    public void setLogIdleTitle(boolean logIdleTitle) {
        this.logIdleTitle = logIdleTitle;
    }

    public boolean isChecksum() {
        return checksum;
    }

    public void setChecksum(boolean checksum) {
        this.checksum = checksum;
    }

    public int getChecksumTimeout() {
        return checksumTimeout;
    }

    public void setChecksumTimeout(int checksumTimeout) {
        this.checksumTimeout = checksumTimeout;
    }
}
