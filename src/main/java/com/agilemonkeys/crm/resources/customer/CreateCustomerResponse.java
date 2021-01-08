package com.agilemonkeys.crm.resources.customer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class CreateCustomerResponse {
    private final UUID customerId;

    @JsonCreator
    public CreateCustomerResponse(@JsonProperty("customerId") UUID customerId) {
        this.customerId = customerId;
    }

    public UUID getCustomerId() {
        return customerId;
    }
}
