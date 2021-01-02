package com.agilemonkeys.crm.resources;

import java.util.UUID;

public class CreateUserResponse {
    private UUID userId;

    public CreateUserResponse(UUID userId) {
        this.userId = userId;
    }

    public UUID getUserId() {
        return userId;
    }
}
