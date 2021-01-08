package com.agilemonkeys.crm.services.user;

import com.agilemonkeys.crm.domain.User;
import com.agilemonkeys.crm.domain.UserBuilder;
import com.agilemonkeys.crm.exceptions.CrmServiceApiDuplicatedException;
import com.agilemonkeys.crm.storage.UsersDao;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.UUID;

public class DuplicatedUserServiceTest {

    private static final UUID USER_ID = UUID.randomUUID();
    private static final User EXISTING_USER = UserBuilder.builder().withUserId(USER_ID).build();
    private static final String USERNAME = "test-username";

    private UsersDao usersDao = Mockito.mock(UsersDao.class);
    private DuplicatedUserService underTest = new DuplicatedUserService(usersDao);

    @Test(expected = CrmServiceApiDuplicatedException.class)
    public void should_throw_duplicated_exception_when_request_username_already_in_database() {
        Mockito.when(usersDao.getUserByUsername(USERNAME)).thenReturn(Optional.of(EXISTING_USER));

        underTest.checkUsername(USERNAME, Optional.empty());
    }

    @Test(expected = CrmServiceApiDuplicatedException.class)
    public void should_throw_duplicated_exception_when_request_username_already_in_database_for_another_user() {
        Mockito.when(usersDao.getUserByUsername(USERNAME)).thenReturn(Optional.of(EXISTING_USER));

        UUID newUserId = UUID.randomUUID();
        underTest.checkUsername(USERNAME, Optional.of(newUserId));
    }

    @Test
    public void should_not_throw_duplicated_exception_when_request_username_not_in_database() {
        Mockito.when(usersDao.getUserByUsername(USERNAME)).thenReturn(Optional.empty());

        underTest.checkUsername(USERNAME, Optional.empty());
        Mockito.verify(usersDao).getUserByUsername(USERNAME);
    }

    @Test
    public void should_not_throw_duplicated_exception_when_request_username_not_in_database_passing_userId() {
        Mockito.when(usersDao.getUserByUsername(USERNAME)).thenReturn(Optional.empty());

        underTest.checkUsername(USERNAME, Optional.of(USER_ID));
        Mockito.verify(usersDao).getUserByUsername(USERNAME);
    }

    @Test
    public void should_not_throw_duplicated_exception_when_request_username_already_in_database_for_the_same_user() {
        Mockito.when(usersDao.getUserByUsername(USERNAME)).thenReturn(Optional.of(EXISTING_USER));

        underTest.checkUsername(USERNAME, Optional.of(USER_ID));
        Mockito.verify(usersDao).getUserByUsername(USERNAME);
    }

}