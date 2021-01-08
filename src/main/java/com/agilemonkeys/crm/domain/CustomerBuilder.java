package com.agilemonkeys.crm.domain;

import com.agilemonkeys.crm.resources.customer.CreateUpdateCustomerRequest;

import java.time.LocalDateTime;
import java.util.UUID;

public class CustomerBuilder {
    private UUID customerId;
    private String providedId;
    private String name;
    private String surname;
    private Integer version = 1;
    private LocalDateTime createdDate = LocalDateTime.now();
    private UUID createdBy;
    private LocalDateTime updatedDate = LocalDateTime.now();
    private UUID updatedBy;

    private CustomerBuilder(){
    }

    public static CustomerBuilder builder() {
        return new CustomerBuilder();
    }

    public static CustomerBuilder from(Customer customer) {
        return new CustomerBuilder().withCustomerId(customer.getCustomerId())
                .withProvidedId(customer.getProvidedId())
                .withName(customer.getName())
                .withSurname(customer.getSurname())
                .withVersion(customer.getVersion())
                .withCreatedDate(customer.getCreatedDate())
                .withCreatedBy(customer.getCreatedBy())
                .withUpdatedDate(customer.getUpdatedDate())
                .withUpdatedBy(customer.getUpdatedBy());
    }

    public static CustomerBuilder from(CreateUpdateCustomerRequest request) {
        return new CustomerBuilder()
                .withProvidedId(request.getProvidedId())
                .withName(request.getName())
                .withSurname(request.getSurname());
    }

    public CustomerBuilder withCustomerId(UUID customerId) {
        this.customerId = customerId;
        return this;
    }

    public CustomerBuilder withProvidedId(String providedId) {
        this.providedId = providedId;
        return this;
    }

    public CustomerBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public CustomerBuilder withSurname(String surname) {
        this.surname = surname;
        return this;
    }

    public CustomerBuilder withVersion(Integer version) {
        this.version = version;
        return this;
    }

    public CustomerBuilder withCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
        return this;
    }

    public CustomerBuilder withCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    public CustomerBuilder withUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
        return this;
    }

    public CustomerBuilder withUpdatedBy(UUID updatedBy) {
        this.updatedBy = updatedBy;
        return this;
    }

    public Customer build() {
        return new Customer(customerId, providedId, name, surname, version, createdDate, createdBy, updatedDate, updatedBy);
    }
}
