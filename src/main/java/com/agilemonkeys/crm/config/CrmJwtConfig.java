package com.agilemonkeys.crm.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.util.Duration;

import javax.validation.constraints.NotNull;

public class CrmJwtConfig {

    public static final String ISSUER = "crm-service-api";
    public static final String CLAIM_ROLE = "role";

    @NotNull
    private String secret;

    @NotNull
    private Duration tokenTimeToLive;

    @JsonProperty
    public String getSecret() {
        return secret;
    }

    @JsonProperty
    public void setSecret(String secret) {
        this.secret = secret;
    }

    @JsonProperty
    public Duration getTokenTimeToLive() {
        return tokenTimeToLive;
    }

    @JsonProperty
    public void setTokenTimeToLive(Duration tokenTimeToLive) {
        this.tokenTimeToLive = tokenTimeToLive;
    }
}
