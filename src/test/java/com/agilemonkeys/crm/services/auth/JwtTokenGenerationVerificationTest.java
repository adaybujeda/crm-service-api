package com.agilemonkeys.crm.services.auth;

import com.agilemonkeys.crm.config.CrmJwtConfig;
import com.agilemonkeys.crm.domain.AuthenticatedUser;
import com.agilemonkeys.crm.domain.CrmAuthToken;
import com.agilemonkeys.crm.domain.UserRole;
import io.dropwizard.util.Duration;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

public class JwtTokenGenerationVerificationTest {

    private JwtTokenFactory tokenFactory;
    private JwtTokenVerifier tokenVerifier;

    @Before
    public void beforeEachTest() {
        CrmJwtConfig config = new CrmJwtConfig();
        config.setSecret("toomanysecrets");
        config.setTokenTimeToLive(Duration.minutes(10));
        tokenFactory = new JwtTokenFactory(config);
        tokenVerifier = new JwtTokenVerifier(config);
    }

    @Test
    public void should_generate_jwt_token_and_verify_token_without_errors() {
        UUID userId = UUID.randomUUID();
        UserRole role = UserRole.ADMIN;
        CrmAuthToken authToken = tokenFactory.createToken(userId, role);
        AuthenticatedUser authenticatedUser = tokenVerifier.verifyToken(authToken.getEncodedJwtToken());

        MatcherAssert.assertThat(authenticatedUser.getUserId(), Matchers.is(userId));
        MatcherAssert.assertThat(authenticatedUser.getRole(), Matchers.is(role));
    }

    @Test
    public void generate_token_for_local_testing() {
        UUID userId = UUID.randomUUID();
        UserRole role = UserRole.ADMIN;
        CrmAuthToken authToken = tokenFactory.createToken(userId, role);

        System.out.println(authToken);
        System.out.println(authToken.getEncodedJwtToken());
    }

}