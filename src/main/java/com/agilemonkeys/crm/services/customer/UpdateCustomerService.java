package com.agilemonkeys.crm.services.customer;

import com.agilemonkeys.crm.domain.Customer;
import com.agilemonkeys.crm.domain.CustomerBuilder;
import com.agilemonkeys.crm.exceptions.CrmServiceApiNotFoundException;
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
    private final DuplicatedIdService duplicatedIdService;

    public UpdateCustomerService(CustomersDao customersDao, DuplicatedIdService duplicatedIdService) {
        this.customersDao = customersDao;
        this.duplicatedIdService = duplicatedIdService;
    }

    public Customer updateCustomer(UUID customerId, Integer requestVersion, CreateUpdateCustomerRequest updateRequest, UUID updatedBy) {
        Customer oldCustomer = checkExistingCustomerState(customerId, requestVersion);
        duplicatedIdService.checkProvidedId(updateRequest.getProvidedId(), Optional.of(customerId));

        Customer newCustomer = CustomerBuilder.from(oldCustomer)
                .withProvidedId(updateRequest.getProvidedId())
                .withName(updateRequest.getName())
                .withSurname(updateRequest.getSurname())
                .withVersion(oldCustomer.getVersion() + 1)
                .withUpdatedDate(LocalDateTime.now())
                .withUpdatedBy(updatedBy)
                .build();

        updateStorage(requestVersion, newCustomer);
        return newCustomer;
    }

    private Customer checkExistingCustomerState(UUID customerId, Integer requestVersion) {
        Optional<Customer> oldCustomer = customersDao.getCustomerById(customerId);
        if (!oldCustomer.isPresent()) {
            throw new CrmServiceApiNotFoundException(String.format("CustomerId: %s not found", customerId));
        }

        if (!oldCustomer.get().getVersion().equals(requestVersion)) {
            log.warn("action=updateCustomer step=checkExistingCustomerState result=staleState customerId={} requestVersion={} dbCustomer={}", customerId, requestVersion, oldCustomer.get());
            throw new CrmServiceApiStaleStateException(String.format("Invalid state for customerId: %s request version: %s found: %s", customerId, requestVersion, oldCustomer.get().getVersion()));
        }

        return oldCustomer.get();
    }

    private void updateStorage(Integer fromVersion, Customer newCustomer) {
        int updated = customersDao.updateCustomer(newCustomer, fromVersion);
        if (updated != 1) {
            throw new CrmServiceApiStaleStateException(String.format("Concurrent modification. CustomerId: %s modified since request was made. Try again", newCustomer.getCustomerId()));
        }
        log.info("action=updateCustomer step=updateStorage result=success customerID={} newCustomer={}", newCustomer.getCustomerId(), newCustomer);
    }
}
