package com.agilemonkeys.crm.services.user;

import com.agilemonkeys.crm.storage.UsersDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class DeleteUserService {

    private static final Logger log = LoggerFactory.getLogger(DeleteUserService.class);

    private final UsersDao usersDao;

    public DeleteUserService(UsersDao usersDao) {
        this.usersDao = usersDao;
    }

    public boolean deleteUser(UUID userId) {
        int deleted = usersDao.deleteUser(userId);
        log.info("action=deleteUser result=success userId={}, deleted={}", userId, deleted);
        return deleted == 1;
    }
}
