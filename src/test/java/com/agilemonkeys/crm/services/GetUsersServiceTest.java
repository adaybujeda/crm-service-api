package com.agilemonkeys.crm.services;

import com.agilemonkeys.crm.domain.User;
import com.agilemonkeys.crm.exceptions.CrmServiceApiNotFoundException;
import com.agilemonkeys.crm.storage.UsersDao;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class GetUsersServiceTest {

    private UsersDao usersDao = Mockito.mock(UsersDao.class);
    private GetUsersService underTest = new GetUsersService(usersDao);


    @Test(expected = CrmServiceApiNotFoundException.class)
    public void should_throw_not_found_exception_when_userId_not_in_database() {
        UUID notFoundId = UUID.randomUUID();
        Mockito.when(usersDao.getUserById(notFoundId)).thenReturn(Optional.empty());

        underTest.getUserById(notFoundId);
    }

    @Test
    public void should_return_user_from_database() {
        UUID userId = UUID.randomUUID();
        User dbUser = Mockito.mock(User.class);
        Mockito.when(usersDao.getUserById(userId)).thenReturn(Optional.of(dbUser));

        User result = underTest.getUserById(userId);
        MatcherAssert.assertThat(result, Matchers.is(dbUser));
    }

    @Test
    public void should_return_all_users_from_database() {
        List<User> dbUsers = Arrays.asList(Mockito.mock(User.class), Mockito.mock(User.class));
        Mockito.when(usersDao.getUsers()).thenReturn(dbUsers);

        List<User> result = underTest.getAllUsers();
        MatcherAssert.assertThat(result.size(), Matchers.is(2));
        MatcherAssert.assertThat(result, Matchers.is(dbUsers));
    }

}