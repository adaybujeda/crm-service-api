package com.agilemonkeys.crm.services.auth;

import com.agilemonkeys.crm.domain.UserRole;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

public class CrmPasswordHashServiceTest {

    private CrmPasswordHashService underTest = new CrmPasswordHashService();

    @Test
    public void should_hash_password_and_verify_it_without_errors() {
        String plainTextPassword = "crm-password";
        UserRole role = UserRole.ADMIN;

        String passwordHash = underTest.hashPassword(plainTextPassword);
        MatcherAssert.assertThat(passwordHash, Matchers.notNullValue());

        MatcherAssert.assertThat(underTest.checkPassword(plainTextPassword, passwordHash), Matchers.is(true));
    }

    @Test
    public void should_return_false_when_password_does_not_match() {
        String plainTextPassword = "crm-password";
        UserRole role = UserRole.ADMIN;

        String passwordHash = underTest.hashPassword(plainTextPassword);
        MatcherAssert.assertThat(passwordHash, Matchers.notNullValue());

        MatcherAssert.assertThat(underTest.checkPassword("different", passwordHash), Matchers.is(false));
    }

}