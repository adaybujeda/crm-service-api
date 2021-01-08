package com.agilemonkeys.crm.services.customer;

import com.agilemonkeys.crm.domain.Customer;
import com.agilemonkeys.crm.domain.CustomerBuilder;
import com.agilemonkeys.crm.exceptions.CrmServiceApiStaleStateException;
import com.agilemonkeys.crm.resources.customer.CreateUpdateCustomerRequest;
import com.agilemonkeys.crm.storage.CustomersDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public class UpdateCustomerService {

    private static final Logger log = LoggerFactory.getLogger(UpdateCustomerService.class);

    private final CustomersDao customersDao;
    private final CheckCustomerStateService checkCustomerStateService;
    private final DuplicatedIdService duplicatedIdService;

    public UpdateCustomerService(CustomersDao customersDao, CheckCustomerStateService checkCustomerStateService, DuplicatedIdService duplicatedIdService) {
        this.customersDao = customersDao;
        this.checkCustomerStateService = checkCustomerStateService;
        this.duplicatedIdService = duplicatedIdService;
    }

    public Customer updateCustomer(UUID customerId, Integer requestVersion, CreateUpdateCustomerRequest updateRequest, UUID updatedBy) {
        Customer oldCustomer = checkCustomerStateService.checkCustomerState(customerId, requestVersion);
        Customer newCustomer = CustomerBuilder.from(oldCustomer)
                .withProvidedId(updateRequest.getProvidedId())
                .withName(updateRequest.getName())
                .withSurname(updateRequest.getSurname())
                .withNextVersion()
                .withUpdatedDate()
                .withUpdatedBy(updatedBy)
                .build();

        updateCustomer(requestVersion, newCustomer);
        return newCustomer;
    }

    public Customer updateCustomer(Integer requestVersion, Customer newCustomer) {
        duplicatedIdService.checkProvidedId(newCustomer.getProvidedId(), Optional.of(newCustomer.getCustomerId()));
        updateStorage(requestVersion, newCustomer);
        return newCustomer;
    }

    private void updateStorage(Integer fromVersion, Customer newCustomer) {
        int updated = customersDao.updateCustomer(newCustomer, fromVersion);
        if (updated != 1) {
            throw new CrmServiceApiStaleStateException(String.format("Concurrent modification. CustomerId: %s modified since request was made. Try again", newCustomer.getCustomerId()));
        }
        log.info("action=updateCustomer step=updateStorage result=success customerID={} newCustomer={}", newCustomer.getCustomerId(), newCustomer);
    }
}
