package com.devclinton.WindowMetrics.tasks.WindowInfo;

public interface WindowInfoInterface {

    long getIdleTime();
    String getActiveWindowTitle();

    ProcessInfo getProcessName();
}