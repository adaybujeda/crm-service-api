package com.agilemonkeys.crm.resources.user;

import com.agilemonkeys.crm.domain.UserRole;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class UpdateUserRequest {

    @NotEmpty
    @Size(min = 1, max = 100)
    private String name;
    @NotEmpty
    @Size(min = 1, max = 100)
    private String username;
    @NotNull
    private UserRole role;

    @JsonCreator
    public UpdateUserRequest(@JsonProperty("name") String name, @JsonProperty("username") String username, @JsonProperty("role") UserRole role) {
        this.name = name;
        this.username = username;
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public UserRole getRole() {
        return role;
    }
}
