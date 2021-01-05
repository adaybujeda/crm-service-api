package com.agilemonkeys.crm.util;

import com.agilemonkeys.crm.resources.auth.LoginResponse;
import com.agilemonkeys.crm.resources.user.UserResponse;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;

import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.util.UUID;

public interface WithVersionedUser extends WithUser{

    public default Builder addVersion(Builder requestBuilder, Integer version) {
        requestBuilder.header(HttpHeaders.IF_MATCH, version);
        return requestBuilder;
    }

    public default VersionedUser getVersionedUser(UUID userId) {
        LoginResponse loginResponse = adminLogin();
        return getVersionedUser(loginResponse, userId);
    }

    public default VersionedUser getVersionedUser(LoginResponse loginResponse, UUID userId) {
        Response httpResponse = getUserResponse(loginResponse, userId);

        MatcherAssert.assertThat(httpResponse.getStatus(), Matchers.is(200));
        UserResponse response = httpResponse.readEntity(UserResponse.class);
        Integer version = Integer.valueOf(httpResponse.getHeaderString(HttpHeaders.ETAG));
        return new VersionedUser(response, version);
    }

    public static class VersionedUser {
        public final UserResponse user;
        public final Integer version;

        public VersionedUser(UserResponse user, Integer version) {
            this.user = user;
            this.version = version;
        }
    }
}
