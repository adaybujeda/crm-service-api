package com.agilemonkeys.crm.component.auth;

import com.agilemonkeys.crm.RunningServiceBaseTest;
import com.agilemonkeys.crm.domain.UserRole;
import com.agilemonkeys.crm.resources.auth.LoginResponse;
import com.agilemonkeys.crm.resources.auth.ResetPasswordRequest;
import com.agilemonkeys.crm.resources.auth.ResetPasswordResource;
import com.agilemonkeys.crm.util.WithAuth;
import com.agilemonkeys.crm.util.WithDeleteUser;
import com.agilemonkeys.crm.util.WithUser;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.util.UUID;

public class ResetPasswordComponentTest extends RunningServiceBaseTest implements WithUser, WithDeleteUser {

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
    public void should_return_401_when_user_is_deleted() {
        String adminUsername = UUID.randomUUID().toString();
        String adminPassword = UUID.randomUUID().toString();
        UUID newAdmin = createUser(adminLogin(), adminUsername, adminPassword, UserRole.ADMIN);
        LoginResponse adminLogin = login(adminUsername, adminPassword);
        deleteUserResponse(adminLogin, newAdmin).close();
        ResetPasswordRequest request = new ResetPasswordRequest("newPassword");
        Response httpResponse = authorizedRequest(adminLogin, ResetPasswordResource.PATH).post(Entity.json(request));

        MatcherAssert.assertThat(httpResponse.getStatus(), Matchers.is(401));
        httpResponse.close();
    }

    @Test
    public void should_return_204_when_successful() {
        ResetPasswordRequest request = new ResetPasswordRequest(WithAuth.ADMIN_PASSWORD);
        Response httpResponse = authorizedRequest(ResetPasswordResource.PATH).post(Entity.json(request));

        MatcherAssert.assertThat(httpResponse.getStatus(), Matchers.is(204));
        httpResponse.close();
    }

    private UUID createRandomUser(LoginResponse loginResponse) {
        return createUser(loginResponse, UUID.randomUUID().toString(), UUID.randomUUID().toString(), UserRole.USER);
    }
}
