package com.sequenceiq.environment.api.info.endpoint;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.sequenceiq.environment.api.info.doc.EnvironmentInfoDescription;
import com.sequenceiq.environment.api.info.doc.EnvironmentInfoOpDescription;
import com.sequenceiq.environment.api.info.model.response.EnvironmentInfoResponse;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Path("/v1/info")
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "/v1/info", description = EnvironmentInfoDescription.ENVIRONMENT_INFO_NOTES, protocols = "http,https")
public interface EnvironmentInfoV1Endpoint {

    @GET
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = EnvironmentInfoOpDescription.GET, notes = EnvironmentInfoDescription.ENVIRONMENT_INFO_NOTES,
            nickname = "info")
    EnvironmentInfoResponse info();

}
