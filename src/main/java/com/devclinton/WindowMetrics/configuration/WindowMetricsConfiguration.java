package com.devclinton.WindowMetrics.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/** Our Window Metric Configuration Object
 *
 * Created by Clinton Collins <clinton.collins@gmail.com> on 9/11/15.
 */
public class WindowMetricsConfiguration extends Configuration {

    @JsonProperty("windowMetricWatcher")
    private WindowMetricWatcher windowMetricWatcher = new WindowMetricWatcher();

    @JsonProperty("swagger")
    private SwaggerBundleConfiguration swaggerBundleConfiguration;

    @Valid
    @NotNull
    private DataSourceFactory database = new DataSourceFactory();

    public Object getDeployName() {
        return "WindowMetrics";
    }

    public SwaggerBundleConfiguration getSwaggerBundleConfiguration() {
        return swaggerBundleConfiguration;
    }

    public void setSwaggerBundleConfiguration(SwaggerBundleConfiguration swaggerBundleConfiguration) {
        this.swaggerBundleConfiguration = swaggerBundleConfiguration;
    }

    public DataSourceFactory getDatabase() {
        return database;
    }

    public void setDatabase(DataSourceFactory database) {
        this.database = database;
    }

    public WindowMetricWatcher getWindowMetricWatcher() {
        return windowMetricWatcher;
    }

    public void setWindowMetricWatcher(WindowMetricWatcher windowMetricWatcher) {
        this.windowMetricWatcher = windowMetricWatcher;
    }
}
