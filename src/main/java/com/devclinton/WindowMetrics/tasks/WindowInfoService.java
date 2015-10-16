package com.devclinton.WindowMetrics.tasks;

import com.devclinton.WindowMetrics.configuration.WindowMetricWatcher;
import com.devclinton.WindowMetrics.dao.WindowMetricDAO;
import com.devclinton.WindowMetrics.models.WindowMetric;
import com.devclinton.WindowMetrics.tasks.WindowInfo.WindowInfoInterface;
import com.devclinton.WindowMetrics.tasks.WindowInfo.Windows;
import com.devclinton.WindowMetrics.tasks.WindowInfo.XInfo;
import com.google.common.util.concurrent.AbstractScheduledService;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.context.internal.ManagedSessionContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * Created by Clinton Collins <clinton.collins@gmail.com> on 10/15/15.
 */
public class WindowInfoService extends AbstractScheduledService {
    private static java.util.logging.Logger m_logger = java.util.logging.Logger.getLogger(WindowInfoService.class.getName());
    private final WindowMetricWatcher config;
    private WindowInfoInterface windowInterface = getWindowInterface();
    private WindowMetricDAO metricDAO;
    private Map<String, ChecksumEntry> m_checksums = new HashMap<String, ChecksumEntry>();
    private SessionFactory sessionFactory;

    public WindowInfoService(WindowMetricWatcher w, SessionFactory sessionFactory, WindowMetricDAO dao) {
        config = w;
        this.sessionFactory = sessionFactory;
        metricDAO = dao;
    }

    public static String getOsName() {
        return System.getProperty("os.name");
    }

    public static boolean isWindows() {
        return getOsName().startsWith("Windows");
    }

    public static WindowInfoInterface getWindowInterface() {
        return (isWindows()) ? new Windows() : new XInfo();
    }

    @Override
    protected void runOneIteration() throws Exception {
        Session session = sessionFactory.openSession();
        try {
            ManagedSessionContext.bind(session);
            int secs = (int) windowInterface.getIdleTime();
            if (secs < config.getIdleAfter() || config.isLogIdleTitle()) {

                WindowMetric metric = new WindowMetric(windowInterface.getProcessName(), windowInterface.getActiveWindowTitle(), config.getInterval() - secs);
                String cksum = "";

                long unixTime = System.currentTimeMillis() / 1000L;
                //if checksum checking is enabled and we have yet to checksum app or
                if (config.isChecksum() && (!m_checksums.containsKey(metric.getExecutable()) ||
                        //or the app's checksum is older than configured timeout
                        (unixTime - ((ChecksumEntry) m_checksums.get(metric.getExecutable())).lastUpdated)
                                / 60 > config.getChecksumTimeout())
                        ) {
                    cksum = getCkSum(metric.getExecutable());
                    ChecksumEntry newChecksum = new ChecksumEntry();
                    newChecksum.checksum = cksum;
                    m_checksums.put(metric.getExecutable(), newChecksum);
                    metric.setCheckSum(cksum);
                }


                if (m_logger.isLoggable(Level.FINE)) {
                    StringBuilder sb = new StringBuilder("Idle:%d(s)\nApp:%s\nTitle:%s");
                    if (config.isChecksum()) {
                        sb.append("\nChecksum:%s");
                    }

                    String msg = config.isChecksum() ? String.format(sb.toString(), metric.getActiveSeconds(), metric.getExecutable(), metric.getWindowTitle(), metric.getCheckSum()) : String.format(sb.toString(), metric.getActiveSeconds(), metric.getExecutable(), metric.getWindowTitle(), metric.getCheckSum());
                    m_logger.fine(msg);
                }
                try {
                    metricDAO.create(metric);
                } catch (HibernateException e) {
                    m_logger.severe(e.toString());
                }
            }
        } finally {
            session.close();
            ManagedSessionContext.unbind(sessionFactory);
        }
    }

    @Override
    protected Scheduler scheduler() {
        return AbstractScheduledService.Scheduler.newFixedRateSchedule(0, config.getInterval(), TimeUnit.SECONDS);
    }

    //TODO - Cleanup
    private String getCkSum(String file) {
        FileInputStream fis;
        try {
            fis = new FileInputStream(new File(file));
            String md5 = org.apache.commons.codec.digest.DigestUtils
                    .md5Hex(fis);
            return md5;
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

    private class ChecksumEntry {
        public String checksum;
        public long lastUpdated = System.currentTimeMillis() / 1000L;
    }
}
