package com.agilemonkeys.crm.resources;

import com.agilemonkeys.crm.domain.UserRole;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CreateUpdateUserRequest {

    @NotEmpty
    @Size(min = 1, max = 100)
    private String name;
    @NotEmpty
    @Size(min = 1, max = 100)
    private String username;
    @NotEmpty
    @Size(min = 6, max = 40)
    private String password;
    @NotNull
    private UserRole role;

    @JsonCreator
    public CreateUpdateUserRequest(@JsonProperty("name") String name, @JsonProperty("username") String username, @JsonProperty("password") String password, @JsonProperty("role") UserRole role) {
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
