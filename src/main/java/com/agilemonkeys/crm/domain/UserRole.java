package com.agilemonkeys.crm.domain;

public enum UserRole {
    ADMIN, USER;

    //KEEP IN SYNC ^^
    public static final String ADMIN_STRING = "ADMIN";
    public static final String USER_STRING = "USER";

    public boolean isEqual(String stringRole) {
        if (stringRole == null || stringRole.isEmpty()) {
            return false;
        }

        String normalizedRole = stringRole.trim().toUpperCase();
        for(UserRole role: UserRole.values()) {
            if (normalizedRole.equals(role.name())) {
                return true;
            }
        }

        return false;
    }
}
