package com.agilemonkeys.crm.resources.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class CreateUserResponse {
    private final UUID userId;

    @JsonCreator
    public CreateUserResponse(@JsonProperty("userId") UUID userId) {
        this.userId = userId;
    }

    public UUID getUserId() {
        return userId;
    }
}
