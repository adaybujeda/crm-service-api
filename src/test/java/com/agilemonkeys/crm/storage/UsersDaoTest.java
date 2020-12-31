package com.agilemonkeys.crm.storage;

import com.agilemonkeys.crm.CrmServiceApiApplication;
import com.agilemonkeys.crm.CrmServiceApiConfiguration;
import com.agilemonkeys.crm.domain.User;
import com.agilemonkeys.crm.domain.UserRole;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.hamcrest.CoreMatchers;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.reflect.ConstructorMapper;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public class UsersDaoTest {

    @ClassRule
    public static final DropwizardAppRule<CrmServiceApiConfiguration> RULE =
            new DropwizardAppRule<>(CrmServiceApiApplication.class, "/config.yml");

    private static UsersDao underTest;

    @BeforeClass
    public static void beforeTest() throws Exception {
        RULE.getApplication().run("db", "migrate", "/config.yml");
        Jdbi jdbi = Jdbi.create("jdbc:h2:mem:test", "sa", "sa");
        jdbi.installPlugin(new SqlObjectPlugin());
        jdbi.registerRowMapper(ConstructorMapper.factory(User.class));

        underTest = jdbi.onDemand(UsersDao.class);
    }


    @Test
    public void should_store_user() {
        User newUser = new User(UUID.randomUUID(), "John Smith", "jsmith", "password", UserRole.USER, 1, LocalDateTime.now(), LocalDateTime.now());
        int rowsInserted = underTest.insertUser(newUser);
        Assert.assertThat(rowsInserted, CoreMatchers.is(1));
        Optional<User> userById = underTest.getUserById(newUser.getUserId());
        Assert.assertThat(userById.isPresent(), CoreMatchers.is(true));
        Assert.assertThat(userById.get(), CoreMatchers.is(newUser));
    }

}