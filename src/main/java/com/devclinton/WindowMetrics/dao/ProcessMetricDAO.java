package com.devclinton.WindowMetrics.dao;

import com.devclinton.WindowMetrics.models.ProcessMetric;
import com.google.common.collect.ImmutableMap;
import org.hibernate.Query;
import org.hibernate.SessionFactory;

/**
 * Created by Clinton Collins <clinton.collins@cgi.com> on 10/16/15.
 */
public class ProcessMetricDAO extends AbstractBasicDao<ProcessMetric> {
    /**
     * Creates a new DAO with a given session provider.
     *
     * @param sessionFactory a session provider
     */
    public ProcessMetricDAO(SessionFactory sessionFactory) {
        super(sessionFactory, ImmutableMap.of(
                "executable", "executable",
                "window_title", "window_title"
        ));
    }

    public ProcessMetric findByExeAndChecksum(String exe, String cksum) {
        Query fpq = currentSession().getNamedQuery("Process.findByIdCkSum");
        fpq.setParameter("exe", exe);
        fpq.setParameter("cksum", cksum);

        ProcessMetric r = (fpq.list().size() > 0) ? (ProcessMetric) fpq.list().get(0) : null;

        return r;
    }
}