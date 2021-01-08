package com.agilemonkeys.crm.resources.user;

import com.agilemonkeys.crm.domain.UserRole;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CreateUserRequest {

    @NotEmpty
    @Size(min = 1, max = 50)
    private final String name;
    @NotEmpty
    @Size(min = 1, max = 50)
    private final String username;
    @NotEmpty
    @Size(min = 6, max = 40)
    private final String password;
    @NotNull
    private final UserRole role;

    @JsonCreator
    public CreateUserRequest(@JsonProperty("name") String name, @JsonProperty("username") String username, @JsonProperty("password") String password, @JsonProperty("role") UserRole role) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public UserRole getRole() {
        return role;
    }
}
