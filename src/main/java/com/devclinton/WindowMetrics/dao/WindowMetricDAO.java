package com.devclinton.WindowMetrics.dao;

import com.devclinton.WindowMetrics.models.WindowMetric;
import com.google.common.collect.ImmutableMap;
import org.hibernate.SessionFactory;

/**
 * Our DAO for doing all our dirty data dealings
 * <p>
 * Created by Clinton Collins <clinton.collins@gmail.com> on 10/15/15.
 */
public class WindowMetricDAO extends AbstractBasicDao<WindowMetric> {

    /**
     * Creates a new DAO with a given session provider.
     *
     * @param sessionFactory a session provider
     */
    public WindowMetricDAO(SessionFactory sessionFactory) {
        super(sessionFactory, ImmutableMap.of(
                "window_title", "window_title"
        ));
    }

    public WindowMetric create(WindowMetric metric) {
        return persist(metric);
    }

}
