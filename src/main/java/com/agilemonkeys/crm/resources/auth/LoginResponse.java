package com.agilemonkeys.crm.resources.auth;

public class LoginResponse {
    private String authToken;
    private long expiresIn;

    public LoginResponse(String authToken, long expiresIn) {
        this.authToken = authToken;
        this.expiresIn = expiresIn;
    }

    public String getAuthToken() {
        return authToken;
    }

    public long getExpiresIn() {
        return expiresIn;
    }
}
