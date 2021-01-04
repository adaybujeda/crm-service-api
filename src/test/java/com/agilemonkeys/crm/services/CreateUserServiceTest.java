package com.agilemonkeys.crm.services;

import com.agilemonkeys.crm.domain.User;
import com.agilemonkeys.crm.domain.UserRole;
import com.agilemonkeys.crm.exceptions.CrmServiceApiDuplicatedException;
import com.agilemonkeys.crm.resources.CreateUserRequest;
import com.agilemonkeys.crm.storage.UsersDao;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Optional;

public class CreateUserServiceTest {

    private UsersDao usersDao = Mockito.mock(UsersDao.class);
    private DuplicatedUserService duplicatedUserService = Mockito.mock(DuplicatedUserService.class);
    private CreateUserService underTest = new CreateUserService(usersDao, duplicatedUserService);


    @Test(expected = CrmServiceApiDuplicatedException.class)
    public void should_propagate_exception_when_duplicatedUserService_throws_one() {
        CreateUserRequest request = new CreateUserRequest("name", "username", "password", UserRole.ADMIN);
        Mockito.doThrow(new CrmServiceApiDuplicatedException("test")).when(duplicatedUserService).checkUsername(request.getUsername(), Optional.empty());

        underTest.createUser(request);
    }

    @Test
    public void should_create_user_from_request_and_populate_default_fields() {
        CreateUserRequest request = new CreateUserRequest("name", "username", "password", UserRole.ADMIN);
        User createdUser = underTest.createUser(request);

        MatcherAssert.assertThat(createdUser.getName(), Matchers.is(request.getName()));
        MatcherAssert.assertThat(createdUser.getUsername(), Matchers.is(request.getUsername()));
        MatcherAssert.assertThat(createdUser.getPassword(), Matchers.is(request.getPassword()));
        MatcherAssert.assertThat(createdUser.getRole(), Matchers.is(request.getRole()));

        MatcherAssert.assertThat(createdUser.getUserId(), Matchers.notNullValue());
        MatcherAssert.assertThat(createdUser.getVersion(), Matchers.is(1));
        MatcherAssert.assertThat(createdUser.getCreatedDate(), Matchers.notNullValue());
        MatcherAssert.assertThat(createdUser.getUpdatedDate(), Matchers.notNullValue());

        Mockito.verify(usersDao).insertUser(Mockito.any(User.class));
        Mockito.verify(duplicatedUserService).checkUsername(request.getUsername(), Optional.empty());
    }

}