package com.agilemonkeys.crm.services;

import com.agilemonkeys.crm.domain.User;
import com.agilemonkeys.crm.exceptions.CrmServiceApiNotFoundException;
import com.agilemonkeys.crm.storage.UsersDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class GetUsersService {

    private static final Logger log = LoggerFactory.getLogger(GetUsersService.class);

    private final UsersDao usersDao;

    public GetUsersService(UsersDao usersDao) {
        this.usersDao = usersDao;
    }

    public User getUser(UUID userId) {
        Optional<User> user = usersDao.getUserById(userId);
        if(!user.isPresent()) {
            throw new CrmServiceApiNotFoundException(String.format("UserId: %s not found", userId));
        }
        log.info("action=getUser result=success userId={} user={}", userId, user.get());
        return user.get();
    }

    public List<User> getAllUsers() {
        List<User> users = usersDao.getUsers();
        log.info("action=getAllUsers result=success items={}", users.size());
        return users;
    }
}
