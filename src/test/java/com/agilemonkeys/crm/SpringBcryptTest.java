package com.agilemonkeys.crm;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCrypt;

public class SpringBcryptTest {

    @Test
    public void bcryptSample() {
        String plain_password = "spring-bcrypt";
        String pw_hash = BCrypt.hashpw(plain_password, BCrypt.gensalt());
        System.out.println(pw_hash);

        boolean result = BCrypt.checkpw(plain_password, pw_hash);

        MatcherAssert.assertThat(result, Matchers.is(true));

    }
}
