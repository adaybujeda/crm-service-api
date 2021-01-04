package com.agilemonkeys.crm.services;

import com.agilemonkeys.crm.domain.User;
import com.agilemonkeys.crm.domain.UserBuilder;
import com.agilemonkeys.crm.resources.CreateUserRequest;
import com.agilemonkeys.crm.storage.UsersDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.UUID;

public class CreateUserService {

    private static final Logger log = LoggerFactory.getLogger(CreateUserService.class);

    private final UsersDao usersDao;
    private final DuplicatedUserService duplicatedUserService;

    public CreateUserService(UsersDao usersDao, DuplicatedUserService duplicatedUserService) {
        this.usersDao = usersDao;
        this.duplicatedUserService = duplicatedUserService;
    }

    public User createUser(CreateUserRequest createRequest) {
        duplicatedUserService.checkUsername(createRequest.getUsername(), Optional.empty());
        UUID newUserId = UUID.randomUUID();
        User newUser = UserBuilder.fromRequest(createRequest).withUserId(newUserId).build();
        usersDao.insertUser(newUser);
        log.info("action=createUser result=success userId={} newUser={}", newUserId, newUser);
        return newUser;
    }
}
