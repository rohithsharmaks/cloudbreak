package com.sequenceiq.environment.api.ldap.endpoint;

import static com.sequenceiq.environment.api.ldap.doc.LdapConfigModelDescription.LDAP_CONFIG_NOTES;
import static com.sequenceiq.environment.api.ldap.doc.LdapConfigModelDescription.LDAP_V4_CONFIG_DESCRIPTION;

import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.sequenceiq.environment.api.EnvironmentNames;
import com.sequenceiq.environment.api.ldap.doc.LdapConfigOpDescription;
import com.sequenceiq.environment.api.ldap.model.request.LdapTestV1Request;
import com.sequenceiq.environment.api.ldap.model.request.LdapV1Request;
import com.sequenceiq.environment.api.ldap.model.response.LdapTestV1Response;
import com.sequenceiq.environment.api.ldap.model.response.LdapV1Response;
import com.sequenceiq.environment.api.ldap.model.response.LdapV1Responses;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


@Path("/v1/ldaps")
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "/v1/ldaps", description = LDAP_V4_CONFIG_DESCRIPTION, protocols = "http,https")
public interface LdapConfigV1Endpoint {

    @GET
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = LdapConfigOpDescription.LIST_BY_WORKSPACE, produces = MediaType.APPLICATION_JSON, notes = LDAP_CONFIG_NOTES,
            nickname = "listLdapsByWorkspace")
    LdapV1Responses list(@PathParam("workspaceId") Long workspaceId, @QueryParam("environment") String environment,
            @QueryParam("attachGlobal") Boolean attachGlobal);

    @GET
    @Path("{name}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = LdapConfigOpDescription.GET_BY_NAME_IN_WORKSPACE, produces = MediaType.APPLICATION_JSON, notes = LDAP_CONFIG_NOTES,
            nickname = "getLdapConfigInWorkspace")
    LdapV1Response get(@PathParam("workspaceId") Long workspaceId, @PathParam("name") String ldapConfigName);

    @POST
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = LdapConfigOpDescription.CREATE_IN_WORKSPACE, produces = MediaType.APPLICATION_JSON, notes = LDAP_CONFIG_NOTES,
            nickname = "createLdapConfigsInWorkspace")
    LdapV1Response post(@PathParam("workspaceId") Long workspaceId, @Valid LdapV1Request request);

    @DELETE
    @Path("{name}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = LdapConfigOpDescription.DELETE_BY_NAME_IN_WORKSPACE, produces = MediaType.APPLICATION_JSON, notes = LDAP_CONFIG_NOTES,
            nickname = "deleteLdapConfigInWorkspace")
    LdapV1Response delete(@PathParam("workspaceId") Long workspaceId, @PathParam("name") String ldapConfigName);

    @DELETE
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = LdapConfigOpDescription.DELETE_MULTIPLE_BY_NAME_IN_WORKSPACE, produces = MediaType.APPLICATION_JSON, notes = LDAP_CONFIG_NOTES,
            nickname = "deleteLdapConfigsInWorkspace")
    LdapV1Responses deleteMultiple(@PathParam("workspaceId") Long workspaceId, Set<String> ldapConfigNames);

    @POST
    @Path("test")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = LdapConfigOpDescription.POST_CONNECTION_TEST, produces = MediaType.APPLICATION_JSON,
            nickname = "postLdapConnectionTestInWorkspace")
    LdapTestV1Response test(@PathParam("workspaceId") Long workspaceId, @Valid LdapTestV1Request ldapValidationRequest);

    @GET
    @Path("{name}/request")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = LdapConfigOpDescription.GET_REQUEST, produces = MediaType.APPLICATION_JSON, notes = LDAP_CONFIG_NOTES,
            nickname = "getLdapRequestByNameAndWorkspaceId")
    LdapV1Request getRequest(@PathParam("workspaceId") Long workspaceId, @PathParam("name") String name);

    @PUT
    @Path("{name}/attach")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = LdapConfigOpDescription.ATTACH_TO_ENVIRONMENTS, produces = MediaType.APPLICATION_JSON, notes = LDAP_CONFIG_NOTES,
            nickname = "attachLdapResourceToEnvironments")
    LdapV1Response attach(@PathParam("workspaceId") Long workspaceId, @PathParam("name") String name,
            @Valid @NotNull EnvironmentNames environmentNames);

    @PUT
    @Path("{name}/detach")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = LdapConfigOpDescription.DETACH_FROM_ENVIRONMENTS, produces = MediaType.APPLICATION_JSON, notes = LDAP_CONFIG_NOTES,
            nickname = "detachLdapResourceFromEnvironments")
    LdapV1Response detach(@PathParam("workspaceId") Long workspaceId, @PathParam("name") String name,
            @Valid @NotNull EnvironmentNames environmentNames);
}
