package com.agilemonkeys.crm.services;

import com.agilemonkeys.crm.domain.User;
import com.agilemonkeys.crm.domain.UserBuilder;
import com.agilemonkeys.crm.resources.CreateUserRequest;
import com.agilemonkeys.crm.services.auth.CrmPasswordHashService;
import com.agilemonkeys.crm.storage.UsersDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.UUID;

public class CreateUserService {

    private static final Logger log = LoggerFactory.getLogger(CreateUserService.class);

    private final UsersDao usersDao;
    private final DuplicatedUserService duplicatedUserService;
    private final CrmPasswordHashService passwordHashService;

    public CreateUserService(UsersDao usersDao, DuplicatedUserService duplicatedUserService, CrmPasswordHashService passwordHashService) {
        this.usersDao = usersDao;
        this.duplicatedUserService = duplicatedUserService;
        this.passwordHashService = passwordHashService;
    }

    public User createUser(CreateUserRequest createRequest) {
        duplicatedUserService.checkUsername(createRequest.getUsername(), Optional.empty());
        UUID newUserId = UUID.randomUUID();
        String passwordHash = passwordHashService.hashPassword(createRequest.getPassword());
        User newUser = UserBuilder.fromRequest(createRequest).withUserId(newUserId)
                .withPasswordHash(passwordHash).build();
        usersDao.insertUser(newUser);
        log.info("action=createUser result=success userId={} newUser={}", newUserId, newUser);
        return newUser;
    }
}
