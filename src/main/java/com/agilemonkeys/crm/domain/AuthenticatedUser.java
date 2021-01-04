package com.agilemonkeys.crm.domain;

import com.google.common.base.MoreObjects;

import java.security.Principal;
import java.util.UUID;

public class AuthenticatedUser implements Principal {

    private final UUID userId;
    private final UserRole role;

    public AuthenticatedUser(UUID userId, UserRole role) {
        this.userId = userId;
        this.role = role;
    }

    public UUID getUserId() {
        return userId;
    }

    public UserRole getRole() {
        return role;
    }

    @Override
    public String getName() {
        return "crm-service-api-principal";
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("userId", userId)
                .add("role", role)
                .add("principal", getName())
                .toString();
    }
}
