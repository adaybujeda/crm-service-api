package com.agilemonkeys.crm.resources.user;

import com.agilemonkeys.crm.domain.User;
import com.agilemonkeys.crm.domain.UserRole;
import com.agilemonkeys.crm.resources.user.UserResponse.UserResponseCollection;
import com.agilemonkeys.crm.services.user.GetUsersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Path(GetUsersResource.PATH)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed(UserRole.ADMIN_STRING)
public class GetUsersResource {

    public static final String PATH = "/crm/users";

    private static final Logger log = LoggerFactory.getLogger(GetUsersResource.class);

    private final GetUsersService getUsersService;

    public GetUsersResource(GetUsersService getUsersService) {
        this.getUsersService = getUsersService;
    }

    @GET
    public UserResponseCollection getUserList() {
        List<UserResponse> responseItems = getUsersService.getAllUsers().stream().map(user -> UserResponse.fromUser(user)).collect(Collectors.toList());
        log.info("action=getUserList result=success responseItems={}", responseItems.size());
        return new UserResponseCollection(responseItems);
    }

    @GET
    @Path("/{userId}")
    public Response getUser(@PathParam("userId") UUID userId) {
        User user = getUsersService.getUserById(userId);
        UserResponse response = UserResponse.fromUser(user);
        log.info("action=getUser result=success userId={}", userId);
        return Response.ok(response).header(HttpHeaders.ETAG, user.getVersion()).build();
    }

    public static String createResourcePath(UUID userId) {
        return String.format("%s/%s", PATH, userId);
    }
}
