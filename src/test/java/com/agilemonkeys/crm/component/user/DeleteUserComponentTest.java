package com.agilemonkeys.crm.component.user;

import com.agilemonkeys.crm.RunningServiceBaseTest;
import com.agilemonkeys.crm.domain.UserRole;
import com.agilemonkeys.crm.resources.auth.LoginResponse;
import com.agilemonkeys.crm.resources.user.DeleteUserResource;
import com.agilemonkeys.crm.util.WithDeleteUser;
import com.agilemonkeys.crm.util.WithUser;
import org.eclipse.jetty.http.HttpStatus;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.util.UUID;

public class DeleteUserComponentTest extends RunningServiceBaseTest implements WithUser, WithDeleteUser {

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
        UUID newUserId = createUser(loginResponse, UUID.randomUUID().toString(), UUID.randomUUID().toString(), UserRole.ADMIN);
        Response httpResponse = deleteUserResponse(loginResponse, newUserId);

        MatcherAssert.assertThat(httpResponse.getStatus(), Matchers.is(204));
        httpResponse.close();
    }

    @Test
    public void should_not_create_user_with_same_username_after_delete() {
        String username = UUID.randomUUID().toString();
        LoginResponse loginResponse = adminLogin();
        UUID user1 = createUser(loginResponse, username, UUID.randomUUID().toString(), UserRole.ADMIN);
        Response httpResponse = deleteUserResponse(loginResponse, user1);
        MatcherAssert.assertThat(httpResponse.getStatus(), Matchers.is(204));
        httpResponse.close();

        Response userResponse = createUserResponse(loginResponse, username, UUID.randomUUID().toString(), UserRole.ADMIN);
        MatcherAssert.assertThat(userResponse.getStatus(), Matchers.is(HttpStatus.CONFLICT_409));
        httpResponse.close();
    }
}
