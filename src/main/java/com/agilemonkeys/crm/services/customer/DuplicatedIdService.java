package com.agilemonkeys.crm.services.customer;

import com.agilemonkeys.crm.domain.Customer;
import com.agilemonkeys.crm.exceptions.CrmServiceApiDuplicatedException;
import com.agilemonkeys.crm.storage.CustomersDao;

import java.util.Optional;
import java.util.UUID;

public class DuplicatedIdService {

    private final CustomersDao customersDao;

    public DuplicatedIdService(CustomersDao customersDao) {
        this.customersDao = customersDao;
    }

    public void checkProvidedId(String newProvidedId, Optional<UUID> forCustomerId) {
        Optional<Customer> customer = customersDao.getCustomerByProvidedId(newProvidedId);
        if (customer.isPresent() && forCustomerId.map(customerId -> !customer.get().getCustomerId().equals(customerId)).orElse(true)) {
            throw new CrmServiceApiDuplicatedException(String.format("duplicated providedId: %s", newProvidedId));
        }
    }
}
