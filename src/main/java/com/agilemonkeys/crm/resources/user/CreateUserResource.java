package com.agilemonkeys.crm.resources.user;

import com.agilemonkeys.crm.domain.User;
import com.agilemonkeys.crm.domain.UserRole;
import com.agilemonkeys.crm.services.user.CreateUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

@Path(CreateUserResource.PATH)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed(UserRole.ADMIN_STRING)
public class CreateUserResource {

    public static final String PATH = "/crm/users";

    private static final Logger log = LoggerFactory.getLogger(CreateUserResource.class);

    private final CreateUserService createUserService;

    public CreateUserResource(CreateUserService createUserService) {
        this.createUserService = createUserService;
    }

    @POST
    public Response createUser(@NotNull @Valid CreateUserRequest request) {
        User createdUser = createUserService.createUser(request);
        CreateUserResponse response = new CreateUserResponse(createdUser.getUserId());
        log.info("action=createUser result=success userId={}", createdUser.getUserId());
        String getUserPath = GetUsersResource.createResourcePath(createdUser.getUserId());
        return Response.created(URI.create(getUserPath)).header(HttpHeaders.ETAG, createdUser.getVersion()).entity(response).build();
    }
}
