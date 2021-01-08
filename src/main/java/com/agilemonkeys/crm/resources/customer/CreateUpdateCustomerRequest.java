package com.agilemonkeys.crm.resources.customer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class CreateUpdateCustomerRequest {

    @NotEmpty
    @Size(max = 50)
    private final String providedId;
    @NotEmpty
    @Size(max = 50)
    private final String name;
    @NotEmpty
    @Size(max = 50)
    private final String surname;

    @JsonCreator
    public CreateUpdateCustomerRequest(@JsonProperty("provideId") String providedId, @JsonProperty("name") String name, @JsonProperty("surname") String surname) {
        this.providedId = providedId;
        this.name = name;
        this.surname = surname;
    }

    public String getProvidedId() {
        return providedId;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }
}
