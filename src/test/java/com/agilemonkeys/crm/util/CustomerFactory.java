package com.agilemonkeys.crm.util;

import com.agilemonkeys.crm.domain.Customer;
import com.agilemonkeys.crm.domain.CustomerBuilder;

import java.util.UUID;

public class CustomerFactory {
    public static Customer valid(UUID customerId) {
        String providedId = UUID.randomUUID().toString();
        String name = UUID.randomUUID().toString();
        String surname = UUID.randomUUID().toString();
        UUID createdUpdatedBy = UUID.randomUUID();
        Customer newCustomer = CustomerBuilder.builder().withCustomerId(customerId).withProvidedId(providedId)
                .withName(name).withSurname(surname)
                .withCreatedBy(createdUpdatedBy).withUpdatedBy(createdUpdatedBy).build();
        return newCustomer;
    }
}
