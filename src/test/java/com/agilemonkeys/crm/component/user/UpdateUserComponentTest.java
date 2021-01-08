package com.agilemonkeys.crm.component.user;

import com.agilemonkeys.crm.RunningServiceBaseTest;
import com.agilemonkeys.crm.domain.UserRole;
import com.agilemonkeys.crm.resources.auth.LoginResponse;
import com.agilemonkeys.crm.resources.user.UpdateRoleRequest;
import com.agilemonkeys.crm.resources.user.UpdateUserRequest;
import com.agilemonkeys.crm.resources.user.UpdateUserResource;
import com.agilemonkeys.crm.util.WithVersionedUser;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.Response;
import java.util.UUID;

public class UpdateUserComponentTest extends RunningServiceBaseTest implements WithVersionedUser {

    private static final UUID NOT_FOUND_USER_ID = UUID.randomUUID();

    @Test
    public void updateUser_should_return_401_when_not_authorized() {
        UpdateUserRequest request = createRandomValidRequest();
        String url = getResourceUrl(UpdateUserResource.createResourcePath(NOT_FOUND_USER_ID));
        Response httpResponse = getClient().target(url).request().put(Entity.json(request));

        MatcherAssert.assertThat(httpResponse.getStatus(), Matchers.is(401));
        httpResponse.close();
    }

    @Test
    public void updateUser_should_return_400_when_invalid_request() {
        UpdateUserRequest request = new UpdateUserRequest("name", null, UserRole.USER);
        Builder requestBuilder = authorizedRequest(UpdateUserResource.createResourcePath(NOT_FOUND_USER_ID));
        addVersion(requestBuilder, 1);
        Response httpResponse = requestBuilder.put(Entity.json(request));

        MatcherAssert.assertThat(httpResponse.getStatus(), Matchers.is(400));
        httpResponse.close();
    }

    @Test
    public void updateUser_should_return_404_when_user_not_found() {
        UpdateUserRequest request = new UpdateUserRequest("updatedAdmin", "admin", UserRole.ADMIN);
        Builder requestBuilder = authorizedRequest(UpdateUserResource.createResourcePath(NOT_FOUND_USER_ID));
        addVersion(requestBuilder, 1);
        Response httpResponse = requestBuilder.put(Entity.json(request));

        MatcherAssert.assertThat(httpResponse.getStatus(), Matchers.is(404));
        httpResponse.close();
    }

    @Test
    public void updateUser_should_return_412_when_version_does_not_match() {
        LoginResponse loginResponse = adminLogin();
        VersionedUser adminUser = getVersionedUser(loginResponse, loginResponse.getUserId());
        String path = UpdateUserResource.createResourcePath(adminUser.user.getUserId());
        UpdateUserRequest request = new UpdateUserRequest("updatedAdmin", "admin", UserRole.ADMIN);
        Builder requestBuilder = authorizedRequest(loginResponse, path);
        addVersion(requestBuilder, 300);
        Response httpResponse = requestBuilder.put(Entity.json(request));

        MatcherAssert.assertThat(httpResponse.getStatus(), Matchers.is(412));
        httpResponse.close();
    }

    @Test
    public void updateUser_should_return_204_when_successful_request() {
        LoginResponse loginResponse = adminLogin();
        VersionedUser adminUser = getVersionedUser(loginResponse, loginResponse.getUserId());
        String path = UpdateUserResource.createResourcePath(adminUser.user.getUserId());
        UpdateUserRequest request = new UpdateUserRequest("updatedAdmin", "admin", UserRole.ADMIN);
        Builder requestBuilder = authorizedRequest(loginResponse, path);
        addVersion(requestBuilder, adminUser.version);
        Response httpResponse = requestBuilder.put(Entity.json(request));

        MatcherAssert.assertThat(httpResponse.getStatus(), Matchers.is(204));
        httpResponse.close();
    }

    @Test
    public void updateRole_should_return_401_when_not_authorized() {
        UpdateRoleRequest request = new UpdateRoleRequest(UserRole.ADMIN);
        String url = getResourceUrl(UpdateUserResource.createResourcePath(NOT_FOUND_USER_ID));
        Response httpResponse = getClient().target(url).request().method("PATCH", Entity.json(request));

        MatcherAssert.assertThat(httpResponse.getStatus(), Matchers.is(401));
        httpResponse.close();
    }

    @Test
    public void updateRole_should_return_400_when_invalid_request() {
        UpdateRoleRequest request = new UpdateRoleRequest(null);
        Builder requestBuilder = authorizedRequest(UpdateUserResource.createResourcePath(NOT_FOUND_USER_ID));
        addVersion(requestBuilder, 1);
        Response httpResponse = requestBuilder.method("PATCH", Entity.json(request));

        MatcherAssert.assertThat(httpResponse.getStatus(), Matchers.is(400));
        httpResponse.close();
    }

    @Test
    public void updateRole_should_return_404_when_user_not_found() {
        UpdateRoleRequest request = new UpdateRoleRequest(UserRole.ADMIN);
        Builder requestBuilder = authorizedRequest(UpdateUserResource.createResourcePath(NOT_FOUND_USER_ID));
        addVersion(requestBuilder, 1);
        Response httpResponse = requestBuilder.method("PATCH", Entity.json(request));

        MatcherAssert.assertThat(httpResponse.getStatus(), Matchers.is(404));
        httpResponse.close();
    }

    @Test
    public void updateRole_should_return_412_when_version_does_not_match() {
        LoginResponse loginResponse = adminLogin();
        VersionedUser adminUser = getVersionedUser(loginResponse, loginResponse.getUserId());
        String path = UpdateUserResource.createResourcePath(adminUser.user.getUserId());
        UpdateRoleRequest request = new UpdateRoleRequest(UserRole.ADMIN);
        Builder requestBuilder = authorizedRequest(loginResponse, path);
        addVersion(requestBuilder, 300);
        Response httpResponse = requestBuilder.method("PATCH", Entity.json(request));

        MatcherAssert.assertThat(httpResponse.getStatus(), Matchers.is(412));
        httpResponse.close();
    }

    @Test
    public void updateRole_should_return_204_when_successful_request() {
        LoginResponse loginResponse = adminLogin();
        VersionedUser adminUser = getVersionedUser(loginResponse, loginResponse.getUserId());
        String path = UpdateUserResource.createResourcePath(adminUser.user.getUserId());
        UpdateRoleRequest request = new UpdateRoleRequest(UserRole.ADMIN);
        Builder requestBuilder = authorizedRequest(loginResponse, path);
        addVersion(requestBuilder, adminUser.version);
        Response httpResponse = requestBuilder.method("PATCH", Entity.json(request));

        MatcherAssert.assertThat(httpResponse.getStatus(), Matchers.is(204));
        httpResponse.close();
    }

    private UpdateUserRequest createRandomValidRequest() {
        UpdateUserRequest request = new UpdateUserRequest(UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UserRole.USER);

        return request;
    }
}
