package com.agilemonkeys.crm.resources.auth;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class LoginResponse {
    private final UUID userId;
    private final String authToken;
    private final long expiresIn;

    @JsonCreator
    public LoginResponse(@JsonProperty("userId") UUID userId, @JsonProperty("authToken") String authToken, @JsonProperty("expiresIn") long expiresIn) {
        this.userId = userId;
        this.authToken = authToken;
        this.expiresIn = expiresIn;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getAuthToken() {
        return authToken;
    }

    public long getExpiresIn() {
        return expiresIn;
    }
}
