package com.agilemonkeys.crm.resources.user;

import com.agilemonkeys.crm.domain.UserRole;
import com.agilemonkeys.crm.services.user.DeleteUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;

@Path(DeleteUserResource.PATH)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed(UserRole.ADMIN_STRING)
public class DeleteUserResource {

    public static final String PATH = "/crm/users";

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

    public static String createResourcePath(UUID userId) {
        return String.format("%s/%s", PATH, userId);
    }
}
