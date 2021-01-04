package com.agilemonkeys.crm.services.auth;

import com.agilemonkeys.crm.domain.AuthenticatedUser;
import com.agilemonkeys.crm.domain.UserRole;
import com.agilemonkeys.crm.exceptions.CrmServiceApiAuthException;
import io.dropwizard.auth.AuthenticationException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.UUID;

public class CrmJwtRequestAuthenticatorTest {

    private JwtTokenVerifier jwtTokenVerifier = Mockito.mock(JwtTokenVerifier.class);
    private CrmJwtRequestAuthenticator underTest = new CrmJwtRequestAuthenticator(jwtTokenVerifier);

    @Test
    public void should_return_auth_user_when_valid_token() throws AuthenticationException {
        String crmJwtToken = "some-token";
        AuthenticatedUser expectedAuthUser = new AuthenticatedUser(UUID.randomUUID(), UserRole.USER);
        Mockito.when(jwtTokenVerifier.verifyToken(crmJwtToken)).thenReturn(expectedAuthUser);

        Optional<AuthenticatedUser> result = underTest.authenticate(crmJwtToken);

        MatcherAssert.assertThat(result.isPresent(), Matchers.is(true));
        MatcherAssert.assertThat(result.get(), Matchers.is(expectedAuthUser));

        Mockito.verify(jwtTokenVerifier).verifyToken(crmJwtToken);
    }

    @Test
    public void should_return_empty_when_no_token_provided() throws AuthenticationException {
        Optional<AuthenticatedUser> result = underTest.authenticate(null);

        MatcherAssert.assertThat(result.isPresent(), Matchers.is(false));
        Mockito.verifyNoInteractions(jwtTokenVerifier);
    }

    @Test(expected = CrmServiceApiAuthException.class)
    public void should_propagate_exceptions_from_JwtTokenVerifier() throws AuthenticationException {
        String crmJwtToken = "some-token";
        AuthenticatedUser expectedAuthUser = new AuthenticatedUser(UUID.randomUUID(), UserRole.USER);
        Mockito.when(jwtTokenVerifier.verifyToken("some-token")).thenThrow(new CrmServiceApiAuthException("test"));

        underTest.authenticate(crmJwtToken);
    }
}