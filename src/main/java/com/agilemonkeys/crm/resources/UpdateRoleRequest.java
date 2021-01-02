package com.agilemonkeys.crm.resources;

import com.agilemonkeys.crm.domain.UserRole;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class UpdateRoleRequest {

    @NotNull
    private UserRole role;

    @JsonCreator
    public UpdateRoleRequest(@JsonProperty("role") UserRole role) {
        this.role = role;
    }

    public UserRole getRole() {
        return role;
    }
}
