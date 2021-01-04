package com.agilemonkeys.crm.services.auth;

import org.springframework.security.crypto.bcrypt.BCrypt;

public class CrmPasswordHashService {

    public String hashPassword(String plainPassword) {
        String passwordHash = BCrypt.hashpw(plainPassword, BCrypt.gensalt());
        return passwordHash;
    }

    public boolean checkPassword(String plainPassword, String passwordHash) {
        boolean isPasswordOK = BCrypt.checkpw(plainPassword, passwordHash);
        return isPasswordOK;
    }
}
