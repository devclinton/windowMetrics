package com.devclinton.WindowMetrics.resources;

import com.codahale.metrics.annotation.Timed;
import com.devclinton.WindowMetrics.dao.WindowMetricDAO;
import com.devclinton.WindowMetrics.models.WindowMetric;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import io.dropwizard.hibernate.UnitOfWork;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.Map;

/**
 * Created by Clinton Collins <clinton.collins@gmail.com> on 10/15/15.
 */
@Path("/windowMetrics")
@Api("/windowMetrics")
@Produces(MediaType.APPLICATION_JSON)
public class WindowMetricsResource {

    private final WindowMetricDAO metricDAO;

    public WindowMetricsResource(WindowMetricDAO metricDAO) {
        this.metricDAO = metricDAO;
    }

    @GET
    @UnitOfWork
    @ApiOperation(value = "searchMetrics",
            notes = "Retrieves a list of Journals  passed on search criteria",
            response = WindowMetric.class,
            responseContainer = "List")
    @Timed(name = "WindowMetrics.searchMetrics")
    public Response searchMetrics(@Context UriInfo uriInfo) throws Exception {
        Map.Entry<Long, List<WindowMetric>> result = metricDAO.findAll(uriInfo.getQueryParameters());
        Response.ResponseBuilder respBuilder = Response.noContent().status(Response.Status.OK);
        respBuilder.header("x-total-count", result.getKey().toString());
        return respBuilder.entity(result.getValue()).build();
    }
}
