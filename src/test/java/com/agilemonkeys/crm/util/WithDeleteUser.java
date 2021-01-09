package com.agilemonkeys.crm.util;

import com.agilemonkeys.crm.domain.UserRole;
import com.agilemonkeys.crm.resources.auth.LoginResponse;
import com.agilemonkeys.crm.resources.user.*;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.util.Optional;
import java.util.UUID;

public interface WithDeleteUser extends WithAuth {

    public default Response deleteUserResponse(LoginResponse loginResponse, UUID userId) {
        String path = DeleteUserResource.createResourcePath(userId);
        Response httpResponse = authorizedRequest(loginResponse, path).delete();

        return httpResponse;
    }
}
