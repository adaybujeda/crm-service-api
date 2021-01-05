package com.agilemonkeys.crm.component.auth;

import com.agilemonkeys.crm.RunningServiceBaseTest;
import com.agilemonkeys.crm.WithAuth;
import com.agilemonkeys.crm.resources.auth.ResetPasswordRequest;
import com.agilemonkeys.crm.resources.auth.ResetPasswordResource;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

public class ResetPasswordComponentTest extends RunningServiceBaseTest {

    @Test
    public void should_return_401_when_not_authorized() {
        String url = getResourceUrl(ResetPasswordResource.PATH);
        ResetPasswordRequest request = new ResetPasswordRequest("newPassword");
        Response httpResponse = getClient().target(url).request().post(Entity.json(request));

        MatcherAssert.assertThat(httpResponse.getStatus(), Matchers.is(401));
        httpResponse.close();
    }

    @Test
    public void should_return_400_when_invalid_request() {
        ResetPasswordRequest request = new ResetPasswordRequest(null);
        Response httpResponse = authorizedRequest(ResetPasswordResource.PATH).post(Entity.json(request));

        MatcherAssert.assertThat(httpResponse.getStatus(), Matchers.is(400));
        httpResponse.close();
    }

    @Test
    public void should_return_204_when_successful() {
        ResetPasswordRequest request = new ResetPasswordRequest(WithAuth.ADMIN_PASSWORD);
        Response httpResponse = authorizedRequest(ResetPasswordResource.PATH).post(Entity.json(request));

        MatcherAssert.assertThat(httpResponse.getStatus(), Matchers.is(204));
        httpResponse.close();
    }
}
