package com.agilemonkeys.crm.acceptance;

import com.agilemonkeys.crm.RunningServiceBaseTest;
import com.agilemonkeys.crm.domain.UserRole;
import com.agilemonkeys.crm.resources.auth.LoginResponse;
import com.agilemonkeys.crm.resources.user.*;
import com.agilemonkeys.crm.util.WithAuth;
import com.agilemonkeys.crm.util.WithVersionedUser;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;
import java.util.UUID;

public class UsersApiAcceptanceTest extends RunningServiceBaseTest implements WithVersionedUser {

    private static final String NEW_ADMIN_USERNAME = UUID.randomUUID().toString();
    private static final String NEW_ADMIN_PASSWORD = "P4ssword!";
    private static final String USER_USERNAME = UUID.randomUUID().toString();
    private static final String USER_PASSWORD = "P4ssword!";

    @Test
    public void user_api_acceptance_test() {
        //CREATE NEW ADMIN USER
        LoginResponse rootAuthInfo = login(WithAuth.ADMIN_USERNAME, WithAuth.ADMIN_PASSWORD);
        UUID adminUserId = createUser(rootAuthInfo, NEW_ADMIN_USERNAME, NEW_ADMIN_PASSWORD, UserRole.ADMIN);

        //LOGIN AS NEW ADMIN
        LoginResponse adminAuthInfo = login(NEW_ADMIN_USERNAME, NEW_ADMIN_PASSWORD);
        //CREATE REGULAR USER
        UUID regularUser = createUser(adminAuthInfo, USER_USERNAME, USER_PASSWORD, UserRole.USER);
        //UPDATE USER
        String newUsernameForUser = "jSmith";
        updateUser(adminAuthInfo, regularUser, newUsernameForUser);
        //VERIFY USER CAN LOG IN
        LoginResponse userAuthInfo = login(newUsernameForUser, USER_PASSWORD);
        MatcherAssert.assertThat(userAuthInfo.getUserId(), Matchers.is(regularUser));
        //DELETE USER
        String deleteUserPath = DeleteUserResource.createResourcePath(userAuthInfo.getUserId());
        Response deleteUserResponse = authorizedRequest(adminAuthInfo, deleteUserPath).delete();
        MatcherAssert.assertThat(deleteUserResponse.getStatus(), Matchers.is(204));
        deleteUserResponse.close();

        //VERIFY USER IS GONE
        Response getUserResponse = getUserResponse(adminAuthInfo, userAuthInfo.getUserId());
        MatcherAssert.assertThat(getUserResponse.getStatus(), Matchers.is(404));
        getUserResponse.close();
    }

    private UUID createUser(LoginResponse authInfo, String username, String password, UserRole role) {
        CreateUserRequest adminUserRequest = new CreateUserRequest(UUID.randomUUID().toString(), username, password, role);
        CreateUserResponse createUserResponse = authorizedRequest(authInfo, CreateUserResource.PATH).post(Entity.json(adminUserRequest)).readEntity(CreateUserResponse.class);
       return createUserResponse.getUserId();
    }

    private void updateUser(LoginResponse authInfo, UUID userId, String newUsername) {
        VersionedUser versionedUser = getVersionedUser(authInfo, userId);
        UpdateUserRequest updateRequest = new UpdateUserRequest(versionedUser.user.getName(), newUsername, versionedUser.user.getRole());
        String path = String.format("%s/%s", UpdateUserResource.PATH, userId);
        Invocation.Builder requestBuilder = authorizedRequest(authInfo, path);
        addVersion(requestBuilder, versionedUser.version);
        Response updateResponse = requestBuilder.put(Entity.json(updateRequest));
        updateResponse.close();
        MatcherAssert.assertThat(updateResponse.getStatus(), Matchers.is(204));
    }
}
