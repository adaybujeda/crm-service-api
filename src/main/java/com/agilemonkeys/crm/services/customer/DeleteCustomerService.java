package com.agilemonkeys.crm.services.customer;

import com.agilemonkeys.crm.storage.CustomersDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class DeleteCustomerService {

    private static final Logger log = LoggerFactory.getLogger(DeleteCustomerService.class);

    private final CustomersDao customersDao;

    public DeleteCustomerService(CustomersDao customersDao) {
        this.customersDao = customersDao;
    }

    public boolean deleteCustomer(UUID customerId) {
        int deleted = customersDao.deleteCustomer(customerId);
        log.info("action=deleteCustomer result=success customerId={}, deleted={}", customerId, deleted);
        return deleted == 1;
    }
}
