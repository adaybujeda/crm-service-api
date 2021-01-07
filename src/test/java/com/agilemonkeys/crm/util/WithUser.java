package com.agilemonkeys.crm.util;

import com.agilemonkeys.crm.resources.auth.LoginResponse;
import com.agilemonkeys.crm.resources.user.GetUsersResource;

import javax.ws.rs.core.Response;
import java.util.UUID;

public interface WithUser extends WithAuth{

    public default Response getUserResponse(UUID userId) {
        LoginResponse loginResponse = adminLogin();
        return getUserResponse(loginResponse, userId);
    }

    public default Response getUserResponse(LoginResponse loginResponse, UUID userId) {
        String path = GetUsersResource.createResourcePath(userId);
        Response httpResponse = authorizedRequest(loginResponse, path).get();

        return httpResponse;
    }
}
