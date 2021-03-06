package com.agilemonkeys.crm.component.user;

import com.agilemonkeys.crm.RunningServiceBaseTest;
import com.agilemonkeys.crm.domain.UserRole;
import com.agilemonkeys.crm.resources.user.CreateUserRequest;
import com.agilemonkeys.crm.resources.user.CreateUserResource;
import com.agilemonkeys.crm.util.WithAuth;
import com.agilemonkeys.crm.util.WithUser;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.util.UUID;

public class CreateUserComponentTest extends RunningServiceBaseTest implements WithUser {

    @Test
    public void should_return_401_when_not_authorized() {
        CreateUserRequest request = createRandomValidRequest(UUID.randomUUID().toString());
        String url = getResourceUrl(CreateUserResource.PATH);
        Response httpResponse = getClient().target(url).request().post(Entity.json(request));

        MatcherAssert.assertThat(httpResponse.getStatus(), Matchers.is(401));
        httpResponse.close();
    }

    @Test
    public void should_return_400_when_invalid_request() {
        CreateUserRequest request = createRandomValidRequest(null);
        Response httpResponse = authorizedRequest(CreateUserResource.PATH).post(Entity.json(request));

        MatcherAssert.assertThat(httpResponse.getStatus(), Matchers.is(400));
        httpResponse.close();
    }

    @Test
    public void should_return_409_when_username_is_already_used() {
        CreateUserRequest request = createRandomValidRequest(WithAuth.ADMIN_USERNAME);
        Response httpResponse = authorizedRequest(CreateUserResource.PATH).post(Entity.json(request));

        MatcherAssert.assertThat(httpResponse.getStatus(), Matchers.is(409));
        httpResponse.close();
    }

    private CreateUserRequest createRandomValidRequest(String username) {
        return new CreateUserRequest(UUID.randomUUID().toString(), username, UUID.randomUUID().toString(), UserRole.USER);
    }

    @Test
    public void should_return_userId_for_successful_request() {
        UUID userId = createUser(adminLogin(), UUID.randomUUID().toString(), UUID.randomUUID().toString(), UserRole.USER);
        MatcherAssert.assertThat(userId, Matchers.notNullValue());
    }
}
