package com.agilemonkeys.crm.util;

import com.agilemonkeys.crm.domain.UserRole;
import com.agilemonkeys.crm.resources.auth.LoginResponse;
import com.agilemonkeys.crm.resources.user.CreateUserRequest;
import com.agilemonkeys.crm.resources.user.CreateUserResource;
import com.agilemonkeys.crm.resources.user.CreateUserResponse;
import com.agilemonkeys.crm.resources.user.GetUsersResource;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.util.UUID;

public interface WithUser extends WithAuth {

    public default Response createUserResponse(LoginResponse loginResponse, String username, String password, UserRole role) {
        CreateUserRequest request = new CreateUserRequest(UUID.randomUUID().toString(),
                username, password, role);
        return authorizedRequest(loginResponse, CreateUserResource.PATH).post(Entity.json(request));
    }

    public default UUID createUser(LoginResponse loginResponse, String username, String password, UserRole role) {
        Response httpResponse = createUserResponse(loginResponse, username, password, role);

        MatcherAssert.assertThat(httpResponse.getStatus(), Matchers.is(201));
        MatcherAssert.assertThat(httpResponse.getHeaderString(HttpHeaders.ETAG), Matchers.is("1"));

        CreateUserResponse response = httpResponse.readEntity(CreateUserResponse.class);
        MatcherAssert.assertThat(httpResponse.getHeaderString(HttpHeaders.LOCATION), Matchers.notNullValue());
        MatcherAssert.assertThat(httpResponse.getHeaderString(HttpHeaders.LOCATION), Matchers.containsString(response.getUserId().toString()));
        MatcherAssert.assertThat(response.getUserId(), Matchers.notNullValue());
        return response.getUserId();
    }

    public default Response getUserResponse(UUID userId) {
        LoginResponse loginResponse = adminLogin();
        return getUserResponse(loginResponse, userId);
    }

    public default Response getUserResponse(LoginResponse loginResponse, UUID userId) {
        String path = GetUsersResource.createResourcePath(userId);
        Response httpResponse = authorizedRequest(loginResponse, path).get();

        return httpResponse;
    }
}
