package com.devclinton.WindowMetrics.tasks;

import com.devclinton.WindowMetrics.configuration.WindowMetricWatcher;
import com.devclinton.WindowMetrics.dao.ArgumentsMetricsDAO;
import com.devclinton.WindowMetrics.dao.ProcessMetricDAO;
import com.devclinton.WindowMetrics.dao.WindowMetricDAO;
import com.devclinton.WindowMetrics.models.ArgumentsMetric;
import com.devclinton.WindowMetrics.models.ProcessMetric;
import com.devclinton.WindowMetrics.models.WindowMetric;
import com.devclinton.WindowMetrics.tasks.WindowInfo.ProcessInfo;
import com.devclinton.WindowMetrics.tasks.WindowInfo.WindowInfoInterface;
import com.devclinton.WindowMetrics.tasks.WindowInfo.Windows;
import com.devclinton.WindowMetrics.tasks.WindowInfo.XInfo;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
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
    private static Cache<ProcessInfo, ProcessMetric> processCache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .build();

    private static Cache<ProcessInfo, ArgumentsMetric> argumentsCache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .build();

    private static Cache<String, ChecksumEntry> checksumCache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .build();

    private static java.util.logging.Logger m_logger = java.util.logging.Logger.getLogger(WindowInfoService.class.getName());
    private final WindowMetricWatcher config;
    private WindowInfoInterface windowInterface = getWindowInterface();

    private ProcessMetricDAO processMetricDAO;
    private WindowMetricDAO windowMetricDAO;
    private ArgumentsMetricsDAO argumentsMetricsDAO;


    private Map<String, ChecksumEntry> m_checksums = new HashMap<String, ChecksumEntry>();
    private SessionFactory sessionFactory;

    public WindowInfoService(WindowMetricWatcher w, SessionFactory sessionFactory, WindowMetricDAO windowMetricDAO, ProcessMetricDAO processMetricDAO, ArgumentsMetricsDAO argumentsMetricsDAO) {
        config = w;
        this.sessionFactory = sessionFactory;
        this.windowMetricDAO = windowMetricDAO;
        this.processMetricDAO = processMetricDAO;
        this.argumentsMetricsDAO = argumentsMetricsDAO;
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
        String cksum = "";
        try {
            ManagedSessionContext.bind(session);
            int secs = (int) windowInterface.getIdleTime();
            if (secs < config.getIdleAfter() || config.isLogIdleTitle()) {

                ProcessInfo info = windowInterface.getProcessName();
                WindowMetric windowMetric = new WindowMetric(windowInterface.getActiveWindowTitle(), config.getInterval() - secs);

                long unixTime = System.currentTimeMillis() / 1000L;
                cksum = checksumExecutable(info, unixTime);

                if (m_logger.isLoggable(Level.FINE)) {
                    StringBuilder sb = new StringBuilder("Idle:%d(s)\nApp:%s\nTitle:%s\nArguments: %s");
                    if (config.isChecksum()) {
                        sb.append("\nChecksum:%s");
                    }

                    String msg = config.isChecksum() ? String.format(sb.toString(), windowMetric.getActiveSeconds(), info.getExecutable(), windowMetric.getWindowTitle(), info.getArguments(), cksum) : String.format(sb.toString(), windowMetric.getActiveSeconds(), info.getExecutable(), windowMetric.getWindowTitle(), info.getArguments());
                    m_logger.fine(msg);
                }
                try {
                    ProcessMetric processMetric = processCache.getIfPresent(info);
                    ArgumentsMetric argumentsMetric = argumentsCache.getIfPresent(info);
                    if (processMetric == null)  // none in processCache, so try to load from db
                    {
                        processMetric = processMetricDAO.findByExeAndChecksum(info.getExecutable(), cksum);
                        if (processMetric == null) { //create one
                            if (m_logger.isLoggable(Level.FINEST)) {
                                m_logger.finest("Create a new process entry for process");
                            }
                            processMetric = new ProcessMetric();
                            processMetric.setExecutable(info.getExecutable());
                            processMetric.setCheckSum(cksum);
                            processMetric = processMetricDAO.create(processMetric);
                        } else {
                            if (m_logger.isLoggable(Level.FINEST)) {
                                m_logger.finest("Process loaded from DB");
                            }
                        }
                        processCache.put(info, processMetric);
                    }

                    //Load a new argument metric if we need to
                    if (argumentsMetric == null) {
                        if (m_logger.isLoggable(Level.FINEST)) {
                            m_logger.finest("Create a new arguments entry for process");
                        }
                        ArgumentsMetric am = new ArgumentsMetric();
                        am.setProcess(processMetric);
                        am.setArguments(info.getArguments());
                        argumentsMetricsDAO.create(am);
                        argumentsCache.put(info, am);
                    }

                    windowMetric.setProcess(processMetric);
                    windowMetricDAO.create(windowMetric);
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

    private String checksumExecutable(ProcessInfo info, long unixTime) {
        String md5 = null;
        FileInputStream fis;
        try {
            ChecksumEntry entry = checksumCache.getIfPresent(info.getExecutable());
            if (config.isChecksum() && (entry == null ||
                    //or the app's checksum is older than configured timeout
                    (unixTime - entry.lastUpdated)
                            / 60 > config.getChecksumTimeout())
                    ) {
                fis = new FileInputStream(new File(info.getExecutable()));
                md5 = org.apache.commons.codec.digest.DigestUtils
                        .md5Hex(fis);
                fis.close();
                if (m_logger.isLoggable(Level.FINEST)) {
                    m_logger.finest(String.format("Running Checksum on file <%s>. Checksum entry for the file %s exist. Entry was last updated %ss", info.getExecutable(), (entry == null) ? "doesn't" : "does", (entry == null) ? "-1" : String.valueOf(unixTime - entry.lastUpdated)));
                }
                ChecksumEntry newChecksum = new ChecksumEntry();
                newChecksum.checksum = md5;
                checksumCache.put(info.getExecutable(), newChecksum);
            } else if (config.isChecksum()) {
                md5 = entry.checksum;
            }

        } catch (FileNotFoundException e1) {
            m_logger.severe(String.format("Could not find the file <%s>", info.getExecutable()));
        } catch (IOException e) {
            m_logger.log(Level.SEVERE, String.format("IO issue with the file <%s>", info.getExecutable()), e);
        }
        return md5;
    }

    private class ChecksumEntry {
        public String checksum;
        public long lastUpdated = System.currentTimeMillis() / 1000L;
    }
}
