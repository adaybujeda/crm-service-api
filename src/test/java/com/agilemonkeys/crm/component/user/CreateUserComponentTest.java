package com.agilemonkeys.crm.component.user;

import com.agilemonkeys.crm.RunningServiceBaseTest;
import com.agilemonkeys.crm.util.WithAuth;
import com.agilemonkeys.crm.domain.UserRole;
import com.agilemonkeys.crm.resources.user.CreateUserRequest;
import com.agilemonkeys.crm.resources.user.CreateUserResource;
import com.agilemonkeys.crm.resources.user.CreateUserResponse;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.util.Optional;
import java.util.UUID;

public class CreateUserComponentTest extends RunningServiceBaseTest {

    @Test
    public void should_return_401_when_not_authorized() {
        CreateUserRequest request = createRandomValidRequest(Optional.empty());
        String url = getResourceUrl(CreateUserResource.PATH);
        Response httpResponse = getClient().target(url).request().post(Entity.json(request));

        MatcherAssert.assertThat(httpResponse.getStatus(), Matchers.is(401));
        httpResponse.close();
    }

    @Test
    public void should_return_400_when_invalid_request() {
        CreateUserRequest request = new CreateUserRequest("name", null, null, UserRole.USER);
        Response httpResponse = authorizedRequest(CreateUserResource.PATH).post(Entity.json(request));

        MatcherAssert.assertThat(httpResponse.getStatus(), Matchers.is(400));
        httpResponse.close();
    }

    @Test
    public void should_return_409_when_username_is_already_used() {
        CreateUserRequest request = createRandomValidRequest(Optional.of(WithAuth.ADMIN_USERNAME));
        Response httpResponse = authorizedRequest(CreateUserResource.PATH).post(Entity.json(request));

        MatcherAssert.assertThat(httpResponse.getStatus(), Matchers.is(409));
        httpResponse.close();
    }

    @Test
    public void should_return_userId_for_successful_request() {
        CreateUserRequest request = createRandomValidRequest(Optional.empty());
        Response httpResponse = authorizedRequest(CreateUserResource.PATH).post(Entity.json(request));

        MatcherAssert.assertThat(httpResponse.getStatus(), Matchers.is(200));
        MatcherAssert.assertThat(httpResponse.getHeaderString(HttpHeaders.ETAG), Matchers.notNullValue());
        CreateUserResponse response = httpResponse.readEntity(CreateUserResponse.class);

        MatcherAssert.assertThat(response.getUserId(), Matchers.notNullValue());
    }

    private CreateUserRequest createRandomValidRequest(Optional<String> username) {
        CreateUserRequest request = new CreateUserRequest(UUID.randomUUID().toString(),
                username.orElse(UUID.randomUUID().toString()),
                UUID.randomUUID().toString(),
                UserRole.USER);

        return request;
    }
}
