package com.agilemonkeys.crm.services.customer;

import com.agilemonkeys.crm.domain.Customer;
import com.agilemonkeys.crm.exceptions.CrmServiceApiNotFoundException;
import com.agilemonkeys.crm.exceptions.CrmServiceApiStaleStateException;
import com.agilemonkeys.crm.storage.CustomersDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.UUID;

public class CheckCustomerStateService {

    private static final Logger log = LoggerFactory.getLogger(CheckCustomerStateService.class);

    private final CustomersDao customersDao;

    public CheckCustomerStateService(CustomersDao customersDao) {
        this.customersDao = customersDao;
    }

    public Customer checkCustomerState(UUID customerId, Integer requestVersion) {
        Optional<Customer> oldCustomer = customersDao.getCustomerById(customerId);
        if (!oldCustomer.isPresent()) {
            throw new CrmServiceApiNotFoundException(String.format("CustomerId: %s not found", customerId));
        }

        if (!oldCustomer.get().getVersion().equals(requestVersion)) {
            log.warn("action=checkCustomerState result=staleState customerId={} requestVersion={} dbCustomer={}", customerId, requestVersion, oldCustomer.get());
            throw new CrmServiceApiStaleStateException(String.format("Invalid state for customerId: %s request version: %s found: %s", customerId, requestVersion, oldCustomer.get().getVersion()));
        }

        return oldCustomer.get();
    }
}
