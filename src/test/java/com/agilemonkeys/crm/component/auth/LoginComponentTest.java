package com.agilemonkeys.crm.component.auth;

import com.agilemonkeys.crm.RunningServiceBaseTest;
import com.agilemonkeys.crm.domain.UserRole;
import com.agilemonkeys.crm.resources.auth.LoginRequest;
import com.agilemonkeys.crm.resources.auth.LoginResource;
import com.agilemonkeys.crm.resources.auth.LoginResponse;
import com.agilemonkeys.crm.util.WithDeleteUser;
import com.agilemonkeys.crm.util.WithUser;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.util.UUID;

public class LoginComponentTest extends RunningServiceBaseTest implements WithUser, WithDeleteUser {

    @Test
    public void should_return_auth_token_on_successful_login() {
        LoginResponse loginResponse = adminLogin();
        MatcherAssert.assertThat(loginResponse.getAuthToken(), Matchers.notNullValue());
        MatcherAssert.assertThat(loginResponse.getExpiresIn(), Matchers.notNullValue());
    }

    @Test
    public void should_return_401_for_invalid_credentials() {
        LoginRequest request = new LoginRequest("invalid", "invalid");
        String url = getResourceUrl(LoginResource.PATH);
        Response httpResponse = getClient().target(url).request().post(Entity.json(request));

        MatcherAssert.assertThat(httpResponse.getStatus(), Matchers.is(401));
        httpResponse.close();
    }

    @Test
    public void should_return_401_for_deletedUser() {
        String adminUsername = UUID.randomUUID().toString();
        String adminPassword = UUID.randomUUID().toString();
        UUID newAdmin = createUser(adminLogin(), adminUsername, adminPassword, UserRole.ADMIN);
        LoginResponse adminLogin = login(adminUsername, adminPassword);
        deleteUserResponse(adminLogin, newAdmin).close();

        LoginRequest request = new LoginRequest(adminUsername, adminPassword);
        String url = getResourceUrl(LoginResource.PATH);
        Response httpResponse = getClient().target(url).request().post(Entity.json(request));

        MatcherAssert.assertThat(httpResponse.getStatus(), Matchers.is(401));
        httpResponse.close();

    }

    @Test
    public void should_return_400_for_missing_fields_in_request() {
        LoginRequest request = new LoginRequest("invalid", null);
        String url = getResourceUrl(LoginResource.PATH);
        Response httpResponse = getClient().target(url).request().post(Entity.json(request));

        MatcherAssert.assertThat(httpResponse.getStatus(), Matchers.is(400));
        httpResponse.close();
    }
}
