package com.agilemonkeys.crm.resources;

import com.agilemonkeys.crm.domain.User;
import com.agilemonkeys.crm.resources.UserResponse.UserResponseCollection;
import com.agilemonkeys.crm.services.GetUsersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Path("/crm/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GetUsersResource {

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
        User user = getUsersService.getUser(userId);
        UserResponse response = UserResponse.fromUser(user);
        log.info("action=getUser result=success userId={}", userId);
        return Response.ok(response).header(HttpHeaders.ETAG, user.getVersion()).build();
    }
}
