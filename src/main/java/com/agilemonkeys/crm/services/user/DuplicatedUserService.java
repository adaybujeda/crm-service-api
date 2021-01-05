package com.agilemonkeys.crm.services.user;

import com.agilemonkeys.crm.domain.User;
import com.agilemonkeys.crm.exceptions.CrmServiceApiDuplicatedException;
import com.agilemonkeys.crm.storage.UsersDao;

import java.util.Optional;
import java.util.UUID;

public class DuplicatedUserService {

    private final UsersDao usersDao;

    public DuplicatedUserService(UsersDao usersDao) {
        this.usersDao = usersDao;
    }

    public void checkUsername(String requestUserName, Optional<UUID> forUserId) {
        Optional<User> userByUsername = usersDao.getUserByUsername(requestUserName);
        if (userByUsername.isPresent() && forUserId.map(userId -> !userByUsername.get().getUserId().equals(userId)).orElse(true)) {
            throw new CrmServiceApiDuplicatedException(String.format("duplicated username: %s", requestUserName));
        }
    }
}
