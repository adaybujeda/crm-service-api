package com.agilemonkeys.crm.resources;

import com.agilemonkeys.crm.domain.User;
import com.agilemonkeys.crm.resources.UserResponse.UserResponseCollection;
import com.agilemonkeys.crm.services.CreateUserService;
import com.agilemonkeys.crm.services.GetUsersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Path("/crm/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UsersResource {

    private static final Logger log = LoggerFactory.getLogger(UsersResource.class);

    private CreateUserService createUserService;
    private GetUsersService getUsersService;

    public UsersResource(CreateUserService createUserService, GetUsersService getUsersService) {
        this.createUserService = createUserService;
        this.getUsersService = getUsersService;
    }

    @POST
    public CreateUserResponse createUser(@Valid CreateUpdateUserRequest request) {
        User createdUser = createUserService.createUser(request);
        CreateUserResponse response = new CreateUserResponse(createdUser.getUserId());
        log.info("action=createUser result=success userId={}", createdUser.getUserId());
        return response;
    }

    @GET
    @Path("/{userId}")
    public UserResponse getUser(@PathParam("userId") UUID userId) {
        User user = getUsersService.getUser(userId);
        UserResponse response = UserResponse.fromUser(user);
        log.info("action=getUser result=success userId={}", userId);
        return response;
    }


    @GET
    public UserResponseCollection getUserList() {
        List<UserResponse> responseItems = getUsersService.getAllUsers().stream().map(user -> UserResponse.fromUser(user)).collect(Collectors.toList());
        log.info("action=getUserList result=success responseItems={}", responseItems.size());
        return new UserResponseCollection(responseItems);
    }
}
