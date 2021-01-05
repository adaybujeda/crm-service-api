package com.agilemonkeys.crm;

import com.agilemonkeys.crm.resources.auth.LoginRequest;
import com.agilemonkeys.crm.resources.auth.LoginResource;
import com.agilemonkeys.crm.resources.auth.LoginResponse;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

public interface WithAuth extends WithRunningService {

    public static final String ADMIN_USERNAME = "admin";
    public static final String ADMIN_PASSWORD = "changeme";

    public default LoginResponse adminLogin() {
        LoginRequest request = new LoginRequest(ADMIN_USERNAME, ADMIN_PASSWORD);
        String loginUrl = getRunningService().getResourceUrl(LoginResource.PATH);
        Response httpResponse = getRunningService().getClient().target(loginUrl).request().post(Entity.json(request));

        MatcherAssert.assertThat(httpResponse.getStatus(), Matchers.is(200));

        LoginResponse loginResponse = httpResponse.readEntity(LoginResponse.class);
        return loginResponse;
    }

    public default Builder authorizedRequest(String path) {
        LoginResponse loginResponse = adminLogin();
        return authorizedRequest(loginResponse, path);
    }

    public default Builder authorizedRequest(LoginResponse loginResponse, String path) {
        String targetUrl = getRunningService().getResourceUrl(path);
        Builder authorizedBuilder = getRunningService().getClient().target(targetUrl).request()
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", loginResponse.getAuthToken()));
        return authorizedBuilder;
    }
}
