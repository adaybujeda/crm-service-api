package com.agilemonkeys.crm.services.auth;

import com.agilemonkeys.crm.domain.AuthenticatedUser;
import io.dropwizard.auth.Authorizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrmRoleBasedRequestAuthorizer implements Authorizer<AuthenticatedUser> {

    private static final Logger log = LoggerFactory.getLogger(CrmRoleBasedRequestAuthorizer.class);

    @Override
    public boolean authorize(AuthenticatedUser authenticatedUser, String stringRole) {
        if (stringRole == null || stringRole.isEmpty()) {
            return false;
        }

        boolean isAuthorized = stringRole.trim().toUpperCase().equals(authenticatedUser.getRole().name());
        log.debug("action=authorize isAuthorized={} authenticatedUser={} stringRole={}", isAuthorized, authenticatedUser, stringRole);
        return isAuthorized;
    }
}
