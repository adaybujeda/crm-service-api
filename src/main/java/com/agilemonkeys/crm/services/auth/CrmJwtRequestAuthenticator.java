package com.agilemonkeys.crm.services.auth;

import com.agilemonkeys.crm.domain.AuthenticatedUser;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;

import java.util.Optional;

public class CrmJwtRequestAuthenticator implements Authenticator<String, AuthenticatedUser> {

    private final JwtTokenVerifier jwtTokenVerifier;

    public CrmJwtRequestAuthenticator(JwtTokenVerifier jwtTokenVerifier) {
        this.jwtTokenVerifier = jwtTokenVerifier;
    }

    @Override
    public Optional<AuthenticatedUser> authenticate(String crmJwtToken) throws AuthenticationException {
        if (crmJwtToken == null || crmJwtToken.isEmpty()) {
            return Optional.empty();
        }

        AuthenticatedUser authenticatedUser = jwtTokenVerifier.verifyToken(crmJwtToken);
        return Optional.of(authenticatedUser);
    }
}
