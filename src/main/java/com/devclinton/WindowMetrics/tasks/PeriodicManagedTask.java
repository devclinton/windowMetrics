package com.devclinton.WindowMetrics.tasks;

import com.google.common.util.concurrent.AbstractScheduledService;
import io.dropwizard.lifecycle.Managed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Clinton Collins <clinton.collins@gmail.com> on 10/15/15.
 */
public class PeriodicManagedTask implements Managed {
    protected final AbstractScheduledService periodicTask;
    private final Logger LOGGER = LoggerFactory.getLogger(PeriodicManagedTask.class);

    public PeriodicManagedTask(AbstractScheduledService t) {
        periodicTask = t;
    }

    @Override
    public void start() throws Exception {
        periodicTask.startAsync().awaitRunning();
    }

    @Override
    public void stop() throws Exception {
        periodicTask.stopAsync().awaitTerminated();
    }
}
