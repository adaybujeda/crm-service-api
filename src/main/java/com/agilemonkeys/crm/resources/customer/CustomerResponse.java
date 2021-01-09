package com.agilemonkeys.crm.resources.customer;

import com.agilemonkeys.crm.domain.Customer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class CustomerResponse {

    private final UUID customerId;
    private final String providedId;
    private final String name;
    private final String surname;
    private final String photoUrl;
    private final LocalDateTime createdDate;
    private final UUID createdBy;
    private final LocalDateTime updatedDate;
    private final UUID updatedBy;

    @JsonCreator
    public CustomerResponse(@JsonProperty("customerId") UUID customerId, @JsonProperty("providedId") String providedId,
                            @JsonProperty("name") String name, @JsonProperty("surname") String surname, @JsonProperty("photoUrl") String photoUrl,
                            @JsonProperty("createdDate") LocalDateTime createdDate, @JsonProperty("createdBy") UUID createdBy,
                            @JsonProperty("updatedDate") LocalDateTime updatedDate, @JsonProperty("updatedBy") UUID updatedBy) {
        this.customerId = customerId;
        this.providedId = providedId;
        this.name = name;
        this.surname = surname;
        this.photoUrl = photoUrl;
        this.createdDate = createdDate;
        this.createdBy = createdBy;
        this.updatedDate = updatedDate;
        this.updatedBy = updatedBy;
    }

    public UUID getCustomerId() {
        return customerId;
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

    public String getPhotoUrl() {
        return photoUrl;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    public UUID getUpdatedBy() {
        return updatedBy;
    }

    public static CustomerResponse from(Customer customer) {
        String photoUrl = customer.getPhotoId() == null ? null : GetCustomerPhotoResource.createResourcePath(customer.getPhotoId());
        return new CustomerResponse(customer.getCustomerId(), customer.getProvidedId(),
                customer.getName(), customer.getSurname(),
                photoUrl,
                customer.getCreatedDate(), customer.getCreatedBy(),
                customer.getUpdatedDate(), customer.getUpdatedBy());
    }

    public static class CustomerResponseCollection {
        private List<CustomerResponse> items;

        @JsonCreator
        public CustomerResponseCollection(@JsonProperty("items") List<CustomerResponse> items) {
            this.items = items;
        }

        public List<CustomerResponse> getItems() {
            return items;
        }
    }
}
