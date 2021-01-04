package com.agilemonkeys.crm.services.auth;

import com.agilemonkeys.crm.domain.AuthenticatedUser;
import com.agilemonkeys.crm.domain.UserRole;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.UUID;

public class CrmRoleBasedRequestAuthorizerTest {

    private CrmRoleBasedRequestAuthorizer underTest = new CrmRoleBasedRequestAuthorizer();

    @Test
    public void should_authorize_user_when_roles_match_case_insensitive() {
        AuthenticatedUser authUser = new AuthenticatedUser(UUID.randomUUID(), UserRole.USER);
        boolean result = underTest.authorize(authUser, "usEr ");

        MatcherAssert.assertThat(result, Matchers.is(true));
    }

    @Test
    public void should_not_authorize_user_when_roles_do_not_match() {
        AuthenticatedUser authUser = new AuthenticatedUser(UUID.randomUUID(), UserRole.USER);
        boolean result = underTest.authorize(authUser, UserRole.ADMIN.name());

        MatcherAssert.assertThat(result, Matchers.is(false));
    }

    @Test
    public void should_not_authorize_user_when_role_is_null() {
        AuthenticatedUser authUser = new AuthenticatedUser(UUID.randomUUID(), UserRole.USER);
        boolean result = underTest.authorize(authUser, null);

        MatcherAssert.assertThat(result, Matchers.is(false));
    }

    @Test
    public void should_not_authorize_user_when_role_is_empty() {
        AuthenticatedUser authUser = new AuthenticatedUser(UUID.randomUUID(), UserRole.USER);
        boolean result = underTest.authorize(authUser, "");

        MatcherAssert.assertThat(result, Matchers.is(false));
    }
}