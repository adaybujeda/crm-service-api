package com.agilemonkeys.crm.component.user;

import com.agilemonkeys.crm.RunningServiceBaseTest;
import com.agilemonkeys.crm.resources.user.GetUsersResource;
import com.agilemonkeys.crm.resources.user.UserResponse;
import com.agilemonkeys.crm.resources.user.UserResponse.UserResponseCollection;
import com.agilemonkeys.crm.resources.auth.LoginResponse;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.util.UUID;

public class GetUsersComponentTest extends RunningServiceBaseTest {

    @Test
    public void getAllUsers_should_return_401_when_not_authorized() {
        String url = getResourceUrl(GetUsersResource.PATH);
        Response httpResponse = getClient().target(url).request().get();

        MatcherAssert.assertThat(httpResponse.getStatus(), Matchers.is(401));
        httpResponse.close();
    }

    @Test
    public void getAllUsers_should_return_the_list_of_users_when_successful() {
        Response httpResponse = authorizedRequest(GetUsersResource.PATH).get();

        MatcherAssert.assertThat(httpResponse.getStatus(), Matchers.is(200));
        UserResponseCollection response = httpResponse.readEntity(UserResponseCollection.class);
        MatcherAssert.assertThat(response.getItems().isEmpty(), Matchers.is(false));
    }

    @Test
    public void getUser_should_return_401_when_not_authorized() {
        UUID userId = UUID.randomUUID();
        String url = getResourceUrl(String.format("%s/%s", GetUsersResource.PATH, userId));
        Response httpResponse = getClient().target(url).request().get();

        MatcherAssert.assertThat(httpResponse.getStatus(), Matchers.is(401));
        httpResponse.close();
    }

    @Test
    public void getUser_should_return_404_when_user_not_found() {
        UUID userId = UUID.randomUUID();
        String path = String.format("%s/%s", GetUsersResource.PATH, userId);
        Response httpResponse = authorizedRequest(path).get();

        MatcherAssert.assertThat(httpResponse.getStatus(), Matchers.is(404));
        httpResponse.close();
    }

    @Test
    public void getUser_should_return_a_user_when_successful() {
        LoginResponse loginResponse = adminLogin();
        String path = String.format("%s/%s", GetUsersResource.PATH, loginResponse.getUserId());
        Response httpResponse = authorizedRequest(loginResponse, path).get();

        MatcherAssert.assertThat(httpResponse.getStatus(), Matchers.is(200));
        MatcherAssert.assertThat(httpResponse.getHeaderString(HttpHeaders.ETAG), Matchers.notNullValue());
        UserResponse response = httpResponse.readEntity(UserResponse.class);
        MatcherAssert.assertThat(response.getUserId(), Matchers.is(loginResponse.getUserId()));
    }
}
