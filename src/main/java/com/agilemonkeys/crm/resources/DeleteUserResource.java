package com.agilemonkeys.crm.resources;

import com.agilemonkeys.crm.domain.UserRole;
import com.agilemonkeys.crm.services.DeleteUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;

@Path("/crm/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed(UserRole.ADMIN_STRING)
public class DeleteUserResource {

    private static final Logger log = LoggerFactory.getLogger(DeleteUserResource.class);

    private DeleteUserService deleteUserService;

    public DeleteUserResource(DeleteUserService deleteUserService) {
        this.deleteUserService = deleteUserService;
    }

    @DELETE
    @Path("/{userId}")
    public Response deleteUser(@PathParam("userId") UUID userId) {
        deleteUserService.deleteUser(userId);
        log.info("action=deleteUser result=success userId={}", userId);
        return Response.noContent().build();
    }
}
