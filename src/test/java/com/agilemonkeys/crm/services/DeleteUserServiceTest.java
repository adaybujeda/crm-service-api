package com.agilemonkeys.crm.services;

import com.agilemonkeys.crm.storage.UsersDao;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.UUID;

public class DeleteUserServiceTest {

    private static final UUID USER_ID = UUID.randomUUID();

    private UsersDao usersDao = Mockito.mock(UsersDao.class);

    private DeleteUserService underTest = new DeleteUserService(usersDao);

    @Test
    public void should_return_true_when_user_is_deleted_in_database() {
        Mockito.when(usersDao.deleteUser(USER_ID)).thenReturn(1);
        boolean result = underTest.deleteUser(USER_ID);

        MatcherAssert.assertThat(result, Matchers.is(true));
    }

    @Test
    public void should_return_false_when_user_is_not_deleted_in_database() {
        Mockito.when(usersDao.deleteUser(USER_ID)).thenReturn(0);
        boolean result = underTest.deleteUser(USER_ID);

        MatcherAssert.assertThat(result, Matchers.is(false));
    }

}