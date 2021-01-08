package com.agilemonkeys.crm.services.customer;

import com.agilemonkeys.crm.domain.Customer;
import com.agilemonkeys.crm.exceptions.CrmServiceApiNotFoundException;
import com.agilemonkeys.crm.storage.CustomersDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class GetCustomersService {

    private static final Logger log = LoggerFactory.getLogger(GetCustomersService.class);

    private final CustomersDao customersDao;

    public GetCustomersService(CustomersDao customersDao) {
        this.customersDao = customersDao;
    }

    public Customer getCustomerById(UUID customerId) {
        Optional<Customer> customer = customersDao.getCustomerById(customerId);
        if(customer.isEmpty()) {
            throw new CrmServiceApiNotFoundException(String.format("CustomerId: %s not found", customerId));
        }
        log.info("action=getCustomerById result=success customerId={} customer={}", customerId, customer.get());
        return customer.get();
    }

    public List<Customer> getAllCustomers() {
        List<Customer> customers = customersDao.getCustomers();
        log.info("action=getAllCustomers result=success items={}", customers.size());
        return customers;
    }
}
