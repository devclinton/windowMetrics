package com.devclinton.WindowMetrics;

import com.devclinton.WindowMetrics.configuration.WindowMetricsConfiguration;
import com.devclinton.WindowMetrics.dao.WindowMetricDAO;
import com.devclinton.WindowMetrics.models.WindowMetric;
import com.devclinton.WindowMetrics.resources.WindowMetricsResource;
import com.devclinton.WindowMetrics.tasks.PeriodicManagedTask;
import com.devclinton.WindowMetrics.tasks.WindowInfoService;
import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.server.DefaultServerFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;

public class WindowMetricsService extends Application<WindowMetricsConfiguration> {

    private final HibernateBundle<WindowMetricsConfiguration> hibernateBundle
            = new HibernateBundle<WindowMetricsConfiguration>(WindowMetric.class) {
        @Override
        public DataSourceFactory getDataSourceFactory(WindowMetricsConfiguration configuration) {
            return configuration.getDatabase();
        }
    };

    public static void main(String[] args) throws Exception {
        new WindowMetricsService().run(args);
    }

    @Override
    public void initialize(Bootstrap<WindowMetricsConfiguration> bootstrap) {
        //Initialize database
        bootstrap.addBundle(new MigrationsBundle<WindowMetricsConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(WindowMetricsConfiguration configuration) {
                return configuration.getDatabase();
            }
        });

        // Enable swagger documentation
        bootstrap.addBundle(new SwaggerBundle<WindowMetricsConfiguration>() {
            @Override
            public SwaggerBundleConfiguration getSwaggerBundleConfiguration(WindowMetricsConfiguration configuration) {
                return configuration.getSwaggerBundleConfiguration();
            }
        });

        bootstrap.addBundle(hibernateBundle);
    }

    @Override
    public void run(WindowMetricsConfiguration appConfig, Environment environment) throws Exception {

        DefaultServerFactory sf = (DefaultServerFactory) appConfig.getServerFactory();
        String rootPath = "/api/v1/" + appConfig.getDeployName() + "/*";
        sf.setJerseyRootPath(rootPath);

        final WindowMetricDAO metricDAO = new WindowMetricDAO(hibernateBundle.getSessionFactory());

        final WindowInfoService windowInfo = new WindowInfoService(appConfig.getWindowMetricWatcher(), metricDAO);
        final Managed windowInfoImplementer = new PeriodicManagedTask(windowInfo);

        environment.lifecycle().manage(windowInfoImplementer);


        environment.jersey().register(new WindowMetricsResource(metricDAO));


    }

}