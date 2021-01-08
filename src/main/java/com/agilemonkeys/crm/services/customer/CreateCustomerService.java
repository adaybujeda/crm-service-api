package com.agilemonkeys.crm.services.customer;

import com.agilemonkeys.crm.domain.Customer;
import com.agilemonkeys.crm.domain.CustomerBuilder;
import com.agilemonkeys.crm.resources.customer.CreateUpdateCustomerRequest;
import com.agilemonkeys.crm.storage.CustomersDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.UUID;

public class CreateCustomerService {

    private static final Logger log = LoggerFactory.getLogger(CreateCustomerService.class);

    private final CustomersDao customersDao;
    private final DuplicatedIdService duplicatedIdService;

    public CreateCustomerService(CustomersDao customersDao, DuplicatedIdService duplicatedIdService) {
        this.customersDao = customersDao;
        this.duplicatedIdService = duplicatedIdService;
    }

    public Customer createCustomer(UUID createdBy, CreateUpdateCustomerRequest createRequest) {
        duplicatedIdService.checkProvidedId(createRequest.getProvidedId(), Optional.empty());
        UUID newCustomerId = UUID.randomUUID();
        Customer newCustomer = CustomerBuilder.from(createRequest).withCustomerId(newCustomerId)
                .withCreatedBy(createdBy).withUpdatedBy(createdBy).build();
        customersDao.insertCustomer(newCustomer);
        log.info("action=createCustomer result=success customerId={} newUser={}", newCustomerId, newCustomer);
        return newCustomer;
    }
}
