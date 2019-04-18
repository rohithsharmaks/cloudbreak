package com.sequenceiq.cloudbreak.api.freeipa;

import static com.sequenceiq.cloudbreak.doc.OperationDescriptions.StackOpDescription.CREATE_IN_WORKSPACE;
import static com.sequenceiq.cloudbreak.doc.OperationDescriptions.StackOpDescription.DELETE_BY_NAME_IN_WORKSPACE;
import static com.sequenceiq.cloudbreak.doc.OperationDescriptions.StackOpDescription.GET_BY_NAME_IN_WORKSPACE;
import static com.sequenceiq.cloudbreak.doc.OperationDescriptions.StackOpDescription.GET_STACK_REQUEST_IN_WORKSPACE;
import static com.sequenceiq.cloudbreak.doc.OperationDescriptions.StackOpDescription.GET_STATUS_BY_NAME;
import static com.sequenceiq.cloudbreak.doc.OperationDescriptions.StackOpDescription.LIST_BY_WORKSPACE;
import static com.sequenceiq.cloudbreak.doc.OperationDescriptions.StackOpDescription.RETRY_BY_NAME_IN_WORKSPACE;
import static com.sequenceiq.cloudbreak.doc.OperationDescriptions.StackOpDescription.START_BY_NAME_IN_WORKSPACE;
import static com.sequenceiq.cloudbreak.doc.OperationDescriptions.StackOpDescription.STOP_BY_NAME_IN_WORKSPACE;
import static com.sequenceiq.cloudbreak.doc.OperationDescriptions.StackOpDescription.SYNC_BY_NAME_IN_WORKSPACE;

import java.util.List;
import java.util.Set;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.response.StackStatusV4Response;
import com.sequenceiq.cloudbreak.doc.ContentType;
import com.sequenceiq.cloudbreak.doc.Notes;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Path("/freeipa/{workspaceId}/")
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "/freeipa/{workspaceId}/", protocols = "http,https")
public interface FreeIpaEndpoint {

    @GET
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = LIST_BY_WORKSPACE, produces = ContentType.JSON, notes = Notes.STACK_NOTES, nickname = "listFreeIpaInWorkspace")
    List<FreeIpaResponse> list(@PathParam("workspaceId") Long workspaceId, @QueryParam("environment") String environment,
            @QueryParam("onlyDatalakes") @DefaultValue("false") Boolean onlyDatalakes);

    @POST
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = CREATE_IN_WORKSPACE, produces = ContentType.JSON, notes = Notes.STACK_NOTES, nickname = "postFreeIpaInWorkspace")
    FreeIpaResponse post(@PathParam("workspaceId") Long workspaceId, @Valid FreeIpaRequest request);

    @GET
    @Path("{name}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = GET_BY_NAME_IN_WORKSPACE, produces = ContentType.JSON, notes = Notes.STACK_NOTES, nickname = "getFreeIpaInWorkspace")
    FreeIpaResponse get(@PathParam("workspaceId") Long workspaceId, @PathParam("name") String name, @QueryParam("entries") Set<String> entries);

    @DELETE
    @Path("{name}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = DELETE_BY_NAME_IN_WORKSPACE, produces = ContentType.JSON, notes = Notes.STACK_NOTES, nickname = "deleteFreeIpaInWorkspace")
    void delete(@PathParam("workspaceId") Long workspaceId, @PathParam("name") String name, @QueryParam("forced") @DefaultValue("false") Boolean forced,
            @QueryParam("deleteDependencies") @DefaultValue("false") Boolean deleteDependencies);

    @PUT
    @Path("{name}/sync")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = SYNC_BY_NAME_IN_WORKSPACE, produces = ContentType.JSON, notes = Notes.STACK_NOTES, nickname = "syncFreeIpaInWorkspace")
    void putSync(@PathParam("workspaceId") Long workspaceId, @PathParam("name") String name);

    @PUT
    @Path("{name}/retry")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = RETRY_BY_NAME_IN_WORKSPACE, produces = ContentType.JSON, notes = Notes.RETRY_STACK_NOTES, nickname = "retryFreeIpaInWorkspace")
    void putRetry(@PathParam("workspaceId") Long workspaceId, @PathParam("name") String name);

    @PUT
    @Path("{name}/stop")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = STOP_BY_NAME_IN_WORKSPACE, produces = ContentType.JSON, notes = Notes.STACK_NOTES, nickname = "stopFreeIpaInWorkspace")
    void putStop(@PathParam("workspaceId") Long workspaceId, @PathParam("name") String name);

    @PUT
    @Path("{name}/start")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = START_BY_NAME_IN_WORKSPACE, produces = ContentType.JSON, notes = Notes.STACK_NOTES, nickname = "startFreeIpaInWorkspace")
    void putStart(@PathParam("workspaceId") Long workspaceId, @PathParam("name") String name);

    @GET
    @Path("{name}/request")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = GET_STACK_REQUEST_IN_WORKSPACE, produces = ContentType.JSON, notes = Notes.STACK_NOTES,
            nickname = "getStackRequestFromNameV4")
    FreeIpaRequest getRequestfromName(@PathParam("workspaceId") Long workspaceId, @PathParam("name") String name);

    @GET
    @Path("{name}/status")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = GET_STATUS_BY_NAME, produces = ContentType.JSON, notes = Notes.STACK_NOTES,
            nickname = "statusStackV4")
    StackStatusV4Response getStatusByName(@PathParam("workspaceId") Long workspaceId, @PathParam("name") String name);
}
