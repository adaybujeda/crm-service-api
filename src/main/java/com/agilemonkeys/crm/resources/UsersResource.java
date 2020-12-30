package com.agilemonkeys.crm.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Collections;
import java.util.List;

@Path("/users")
public class UsersResource {


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<?> getUserList() {
        return Collections.emptyList();
    }
}
