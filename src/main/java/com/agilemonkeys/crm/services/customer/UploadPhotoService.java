package com.agilemonkeys.crm.services.customer;

import com.agilemonkeys.crm.domain.Customer;
import com.agilemonkeys.crm.domain.CustomerBuilder;
import com.agilemonkeys.crm.domain.CustomerPhoto;
import com.agilemonkeys.crm.storage.CustomerPhotosDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.UUID;

public class UploadPhotoService {

    private static final Logger log = LoggerFactory.getLogger(UploadPhotoService.class);

    private final CustomerPhotosDao customerPhotosDao;
    private final CheckCustomerStateService checkCustomerStateService;
    private final UpdateCustomerService updateCustomerService;

    public UploadPhotoService(CustomerPhotosDao customerPhotosDao, CheckCustomerStateService checkCustomerStateService, UpdateCustomerService updateCustomerService) {
        this.customerPhotosDao = customerPhotosDao;
        this.checkCustomerStateService = checkCustomerStateService;
        this.updateCustomerService = updateCustomerService;
    }

    public CustomerPhoto createPhotoForCustomer(UUID customerId, Integer requestVersion, String fileType, byte[] photoData, UUID createdBy) {
        Customer customer = checkCustomerStateService.checkCustomerState(customerId, requestVersion);

        CustomerPhoto customerPhoto = new CustomerPhoto(customerId, fileType, photoData, LocalDateTime.now());
        if(customerPhotosDao.getCustomerPhoto(customerId).isEmpty()) {
            customerPhotosDao.insertCustomerPhoto(customerPhoto);
            log.info("action=createImage insert-photo customerId={}", customerId, customerPhoto);
        } else {
            customerPhotosDao.updateCustomerPhoto(customerPhoto);
            log.info("action=createImage update-photo customerId={}", customerId, customerPhoto);
        }

        Customer updatedCustomer = CustomerBuilder.from(customer).withNextVersion()
                .withPhotoId(customerPhoto.getCustomerId())
                .withUpdatedDate().withUpdatedBy(createdBy).build();

        updateCustomerService.updateCustomer(requestVersion, updatedCustomer);
        log.info("action=createImage result=success customerId={} customerPhoto={} customer={}", customerId, customerPhoto, updatedCustomer);
        return customerPhoto;
    }
}
