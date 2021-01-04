package com.agilemonkeys.crm.services;

import com.agilemonkeys.crm.domain.User;
import com.agilemonkeys.crm.domain.UserBuilder;
import com.agilemonkeys.crm.exceptions.CrmServiceApiDuplicatedException;
import com.agilemonkeys.crm.exceptions.CrmServiceApiStaleStateException;
import com.agilemonkeys.crm.storage.UsersDao;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.UUID;

public class UpdateUserServiceUpdateFromUserTest {

    private static final UUID USER_ID = UUID.randomUUID();

    private UsersDao usersDao = Mockito.mock(UsersDao.class);
    private DuplicatedUserService duplicatedUserService = Mockito.mock(DuplicatedUserService.class);

    private UpdateUserService underTest = new UpdateUserService(usersDao, duplicatedUserService);

    @Test(expected = CrmServiceApiStaleStateException.class)
    public void updateUser_should_throw_stale_state_exception_when_database_update_returns_0() {
        Integer version = 10;
        Mockito.when(usersDao.updateUser(Mockito.any(User.class), Mockito.eq(version))).thenReturn(0);

        User newUser = UserBuilder.builder().withUserId(USER_ID).withVersion(version + 1).build();
        underTest.updateUser(version, newUser);
    }

    @Test(expected = CrmServiceApiDuplicatedException.class)
    public void updateUser_should_propagate_exception_when_duplicatedUserService_throws_one() {
        Integer version = 10;
        User newUser = UserBuilder.builder().withUserId(USER_ID)
                .withNormalizedUsername("username").withVersion(version + 1).build();
        Mockito.doThrow(new CrmServiceApiDuplicatedException("test")).when(duplicatedUserService).checkUsername(newUser.getUsername(), Optional.of(USER_ID));

        underTest.updateUser(version, newUser);
    }

    @Test
    public void updateUser_should_update_user_when_version_matches() {
        Integer version = 10;
        Mockito.when(usersDao.updateUser(Mockito.any(User.class), Mockito.eq(version))).thenReturn(1);

        User newUser = UserBuilder.builder().withUserId(USER_ID).withVersion(version + 1).build();
        User result = underTest.updateUser(version, newUser);

        MatcherAssert.assertThat(result, Matchers.is(newUser));
        Mockito.verify(duplicatedUserService).checkUsername(newUser.getUsername(), Optional.of(newUser.getUserId()));
        Mockito.verify(usersDao).updateUser(newUser, version);

    }
}