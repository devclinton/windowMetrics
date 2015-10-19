package com.devclinton.WindowMetrics.dao;

import com.devclinton.WindowMetrics.models.ArgumentsMetric;
import com.google.common.collect.ImmutableMap;
import org.hibernate.SessionFactory;

/**
 * Created by Clinton Collins <clinton.collins@cgi.com> on 10/18/15.
 */
public class ArgumentsMetricsDAO extends AbstractBasicDao<ArgumentsMetric> {
    /**
     * Creates a new DAO with a given session provider.
     *
     * @param sessionFactory a session provider
     */
    public ArgumentsMetricsDAO(SessionFactory sessionFactory) {
        super(sessionFactory, ImmutableMap.of(
                "arguments", "arguments"
        ));
    }
}
