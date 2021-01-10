package com.agilemonkeys.crm.resources.user;

import com.agilemonkeys.crm.domain.User;
import com.agilemonkeys.crm.domain.UserRole;
import com.agilemonkeys.crm.services.user.UpdateUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;

@Path(UpdateUserResource.PATH)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed(UserRole.ADMIN_STRING)
public class UpdateUserResource {

    public static final String PATH = "/crm/users";

    private static final Logger log = LoggerFactory.getLogger(UpdateUserResource.class);

    private final UpdateUserService updateUserService;

    public UpdateUserResource(UpdateUserService updateUserService) {
        this.updateUserService = updateUserService;
    }

    @PUT
    @Path("/{userId}")
    public Response updateUser(@PathParam("userId") UUID userId, @NotNull @HeaderParam(HttpHeaders.IF_MATCH) Integer version, @NotNull @Valid UpdateUserRequest request) {
        User updatedUser = updateUserService.updateUser(userId, version, request);
        log.info("action=updateUser result=success userId={}", userId);
        return Response.noContent().build();
    }

    @PATCH
    @Path("/{userId}")
    public Response updateRole(@PathParam("userId") UUID userId, @NotNull @HeaderParam(HttpHeaders.IF_MATCH) Integer version, @NotNull @Valid UpdateRoleRequest request) {
        User updatedUser = updateUserService.updateRole(userId, version, request.getRole());
        log.info("action=updateRole result=success userId={}", userId);
        return Response.noContent().build();
    }

    public static String createResourcePath(UUID userId) {
        return String.format("%s/%s", PATH, userId);
    }
}
