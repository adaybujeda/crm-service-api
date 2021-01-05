package com.agilemonkeys.crm.resources.auth;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class ResetPasswordRequest {
    @NotEmpty
    @Size(min = 6, max = 40)
    private final String newPassword;

    @JsonCreator
    public ResetPasswordRequest(@JsonProperty("newPassword")  String newPassword) {
        this.newPassword = newPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }
}
