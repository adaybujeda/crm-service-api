package com.agilemonkeys.crm.services.user;

import com.agilemonkeys.crm.domain.User;
import com.agilemonkeys.crm.domain.UserRole;
import com.agilemonkeys.crm.exceptions.CrmServiceApiDuplicatedException;
import com.agilemonkeys.crm.resources.user.CreateUserRequest;
import com.agilemonkeys.crm.services.auth.CrmPasswordHashService;
import com.agilemonkeys.crm.services.user.CreateUserService;
import com.agilemonkeys.crm.services.user.DuplicatedUserService;
import com.agilemonkeys.crm.storage.UsersDao;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.UUID;

public class CreateUserServiceTest {

    private UsersDao usersDao = Mockito.mock(UsersDao.class);
    private DuplicatedUserService duplicatedUserService = Mockito.mock(DuplicatedUserService.class);
    private CrmPasswordHashService passwordHashService = Mockito.mock(CrmPasswordHashService.class);

    private CreateUserService underTest = new CreateUserService(usersDao, duplicatedUserService, passwordHashService);

    @Test(expected = CrmServiceApiDuplicatedException.class)
    public void should_propagate_exception_when_duplicatedUserService_throws_one() {
        CreateUserRequest request = new CreateUserRequest("name", "username", "password", UserRole.ADMIN);
        Mockito.doThrow(new CrmServiceApiDuplicatedException("test")).when(duplicatedUserService).checkUsername(request.getUsername(), Optional.empty());

        underTest.createUser(request);
    }

    @Test
    public void should_create_user_from_request_and_populate_default_fields() {
        CreateUserRequest request = new CreateUserRequest("name", "username", "password", UserRole.ADMIN);
        String passwordHash = UUID.randomUUID().toString();
        Mockito.when(passwordHashService.hashPassword(request.getPassword())).thenReturn(passwordHash);
        User createdUser = underTest.createUser(request);

        MatcherAssert.assertThat(createdUser.getName(), Matchers.is(request.getName()));
        MatcherAssert.assertThat(createdUser.getUsername(), Matchers.is(request.getUsername()));
        MatcherAssert.assertThat(createdUser.getRole(), Matchers.is(request.getRole()));

        MatcherAssert.assertThat(createdUser.getUserId(), Matchers.notNullValue());
        MatcherAssert.assertThat(createdUser.getPasswordHash(), Matchers.is(passwordHash));
        MatcherAssert.assertThat(createdUser.getVersion(), Matchers.is(1));
        MatcherAssert.assertThat(createdUser.getCreatedDate(), Matchers.notNullValue());
        MatcherAssert.assertThat(createdUser.getUpdatedDate(), Matchers.notNullValue());

        Mockito.verify(usersDao).insertUser(Mockito.any(User.class));
        Mockito.verify(duplicatedUserService).checkUsername(request.getUsername(), Optional.empty());
        Mockito.verify(passwordHashService).hashPassword(request.getPassword());
    }

}