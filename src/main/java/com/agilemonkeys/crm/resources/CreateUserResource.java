package com.agilemonkeys.crm.resources;

import com.agilemonkeys.crm.domain.User;
import com.agilemonkeys.crm.services.CreateUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/crm/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CreateUserResource {

    private static final Logger log = LoggerFactory.getLogger(CreateUserResource.class);

    private final CreateUserService createUserService;

    public CreateUserResource(CreateUserService createUserService) {
        this.createUserService = createUserService;
    }

    @POST
    public CreateUserResponse createUser(@Valid CreateUpdateUserRequest request) {
        User createdUser = createUserService.createUser(request);
        CreateUserResponse response = new CreateUserResponse(createdUser.getUserId());
        log.info("action=createUser result=success userId={}", createdUser.getUserId());
        return response;
    }
}
