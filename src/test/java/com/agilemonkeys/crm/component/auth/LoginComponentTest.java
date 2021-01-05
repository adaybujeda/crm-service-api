package com.agilemonkeys.crm.component.auth;

import com.agilemonkeys.crm.RunningServiceBaseTest;
import com.agilemonkeys.crm.resources.auth.LoginRequest;
import com.agilemonkeys.crm.resources.auth.LoginResource;
import com.agilemonkeys.crm.resources.auth.LoginResponse;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

public class LoginComponentTest extends RunningServiceBaseTest {

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
    public void should_return_400_for_missing_fields_in_request() {
        LoginRequest request = new LoginRequest("invalid", null);
        String url = getResourceUrl(LoginResource.PATH);
        Response httpResponse = getClient().target(url).request().post(Entity.json(request));

        MatcherAssert.assertThat(httpResponse.getStatus(), Matchers.is(400));
        httpResponse.close();
    }
}
