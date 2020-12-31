package com.agilemonkeys.crm.domain;

import java.util.UUID;

public class UserId {
    private UUID value;

    private UserId(UUID value) {
        this.value = value;
    }

    public static final UserId create() {
        return new UserId(UUID.randomUUID());
    }

    public static final UserId fromString(String userId) {
        return new UserId(UUID.fromString(userId));
    }

}
