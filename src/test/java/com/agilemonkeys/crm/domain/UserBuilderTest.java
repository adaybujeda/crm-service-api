package com.agilemonkeys.crm.domain;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

public class UserBuilderTest {

    @Test
    public void username_should_always_be_lowercase() {
        User result = new UserBuilder().withNormalizedUsername("SomeBadCasing").build();

        MatcherAssert.assertThat(result.getUsername(), Matchers.is("somebadcasing"));
    }

}