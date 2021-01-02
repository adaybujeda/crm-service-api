package com.agilemonkeys.crm.services;

import com.agilemonkeys.crm.domain.User;
import com.agilemonkeys.crm.domain.UserBuilder;
import com.agilemonkeys.crm.resources.CreateUpdateUserRequest;
import com.agilemonkeys.crm.storage.UsersDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class CreateUserService {

    private static final Logger log = LoggerFactory.getLogger(CreateUserService.class);

    private final UsersDao usersDao;

    public CreateUserService(UsersDao usersDao) {
        this.usersDao = usersDao;
    }

    public User createUser(CreateUpdateUserRequest createRequest) {
        UUID newUserId = UUID.randomUUID();
        User newUser = UserBuilder.fromRequest(createRequest).withUserId(newUserId).build();
        usersDao.insertUser(newUser);
        log.info("action=createUser result=success userId={} newUser={}", newUserId, newUser);
        return newUser;
    }
}
