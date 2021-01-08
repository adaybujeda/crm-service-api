package com.agilemonkeys.crm.component.user;

import com.agilemonkeys.crm.RunningServiceBaseTest;
import com.agilemonkeys.crm.resources.auth.LoginResponse;
import com.agilemonkeys.crm.resources.user.DeleteUserResource;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.util.UUID;

public class DeleteUserComponentTest extends RunningServiceBaseTest {

    @Test
    public void should_return_401_when_not_authorized() {
        String url = getResourceUrl(DeleteUserResource.createResourcePath(UUID.randomUUID()));
        Response httpResponse = getClient().target(url).request().delete();

        MatcherAssert.assertThat(httpResponse.getStatus(), Matchers.is(401));
        httpResponse.close();
    }

    @Test
    public void should_return_204_when_successful() {
        LoginResponse loginResponse = adminLogin();
        String path = DeleteUserResource.createResourcePath(loginResponse.getUserId());
        Response httpResponse = authorizedRequest(loginResponse, path).delete();

        MatcherAssert.assertThat(httpResponse.getStatus(), Matchers.is(204));
        httpResponse.close();
    }
}
