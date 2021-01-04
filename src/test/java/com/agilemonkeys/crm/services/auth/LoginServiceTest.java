package com.agilemonkeys.crm.services.auth;

import com.agilemonkeys.crm.domain.CrmAuthToken;
import com.agilemonkeys.crm.domain.User;
import com.agilemonkeys.crm.domain.UserBuilder;
import com.agilemonkeys.crm.exceptions.CrmServiceApiAuthException;
import com.agilemonkeys.crm.resources.auth.LoginRequest;
import com.agilemonkeys.crm.services.GetUsersService;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.UUID;

public class LoginServiceTest {
    private static final User USER = UserBuilder.builder().withUserId(UUID.randomUUID()).build();

    private GetUsersService getUsersService = Mockito.mock(GetUsersService.class);
    private JwtTokenFactory jwtTokenFactory = Mockito.mock(JwtTokenFactory.class);
    private CrmPasswordHashService crmPasswordHashService= Mockito.mock(CrmPasswordHashService.class);


    private LoginService underTest = new LoginService(getUsersService, jwtTokenFactory, crmPasswordHashService);

    @Test
    public void should_return_auth_token_for_valid_credentials() {
        LoginRequest loginRequest = new LoginRequest("username", "password");
        Mockito.when(getUsersService.getUserByUsername("username")).thenReturn(Optional.of(USER));
        CrmAuthToken generatedToken = Mockito.mock(CrmAuthToken.class);
        Mockito.when(jwtTokenFactory.createToken(USER.getUserId(), USER.getRole())).thenReturn(generatedToken);
        Mockito.when(crmPasswordHashService.checkPassword(loginRequest.getPassword(), USER.getPasswordHash())).thenReturn(true);

        CrmAuthToken result = underTest.login(loginRequest);
        MatcherAssert.assertThat(result, Matchers.is(generatedToken));

        Mockito.verify(getUsersService).getUserByUsername("username");
        Mockito.verify(jwtTokenFactory).createToken(USER.getUserId(), USER.getRole());
    }

    @Test(expected = CrmServiceApiAuthException.class)
    public void should_throw_auth_exception_when_username_not_found() {
        LoginRequest loginRequest = new LoginRequest("username", "password");
        Mockito.when(getUsersService.getUserByUsername("username")).thenReturn(Optional.empty());

        underTest.login(loginRequest);
    }

    @Test(expected = CrmServiceApiAuthException.class)
    public void should_throw_auth_exception_when_password_does_not_match() {
        LoginRequest loginRequest = new LoginRequest("username", "password");
        Mockito.when(getUsersService.getUserByUsername("username")).thenReturn(Optional.of(USER));

        Mockito.when(crmPasswordHashService.checkPassword(loginRequest.getPassword(), USER.getPasswordHash())).thenReturn(false);

        underTest.login(loginRequest);
    }
}