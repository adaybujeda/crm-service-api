package com.agilemonkeys.crm.resources.auth;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;

public class LoginRequest {
    @NotEmpty
    private final String username;
    @NotEmpty
    private final String password;

    @JsonCreator
    public LoginRequest(@JsonProperty("username")  String username, @JsonProperty("password")  String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
