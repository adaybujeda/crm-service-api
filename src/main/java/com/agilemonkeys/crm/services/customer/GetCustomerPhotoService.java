package com.agilemonkeys.crm.services.customer;

import com.agilemonkeys.crm.domain.CustomerPhoto;
import com.agilemonkeys.crm.exceptions.CrmServiceApiNotFoundException;
import com.agilemonkeys.crm.storage.CustomerPhotosDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.UUID;

public class GetCustomerPhotoService {

    private static final Logger log = LoggerFactory.getLogger(GetCustomerPhotoService.class);

    private final CustomerPhotosDao customerPhotosDao;

    public GetCustomerPhotoService(CustomerPhotosDao customerPhotosDao) {
        this.customerPhotosDao = customerPhotosDao;
    }

    public CustomerPhoto getCustomerPhoto(UUID customerId) {
        Optional<CustomerPhoto> customerPhoto = customerPhotosDao.getCustomerPhoto(customerId);
        if(customerPhoto.isEmpty()) {
            throw new CrmServiceApiNotFoundException(String.format("CustomerPhoto not found, customerId: %s", customerId));
        }
        log.info("action=getCustomerPhoto result=success customerId={} customerPhoto={}", customerId, customerPhoto.get());
        return customerPhoto.get();
    }
}
