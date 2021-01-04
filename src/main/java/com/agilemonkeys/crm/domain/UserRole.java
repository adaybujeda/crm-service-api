package com.agilemonkeys.crm.domain;

public enum UserRole {
    ADMIN, USER;

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
