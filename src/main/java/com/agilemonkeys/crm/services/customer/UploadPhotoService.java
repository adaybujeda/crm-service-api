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

    public UploadPhotoService(CustomerPhotosDao customerPhotosDao, CheckCustomerStateService checkCustomerStateService) {
        this.customerPhotosDao = customerPhotosDao;
        this.checkCustomerStateService = checkCustomerStateService;
    }

    public CustomerPhoto createPhotoForCustomer(UUID customerId, Integer requestVersion, String fileType, byte[] photoData, UUID createdBy) {
        Customer customer = checkCustomerStateService.checkCustomerState(customerId, requestVersion);

        CustomerPhoto customerPhoto = new CustomerPhoto(customerId, fileType, photoData, LocalDateTime.now());
        Customer updatedCustomer = CustomerBuilder.from(customer).withNextVersion()
                .withPhotoId(customerPhoto.getCustomerId())
                .withUpdatedDate().withUpdatedBy(createdBy).build();

        customerPhotosDao.updateCustomerWithNewPhoto(updatedCustomer, customerPhoto, requestVersion);
        log.info("action=createImage result=success customerId={} customerPhoto={} customer={}", customerId, customerPhoto, updatedCustomer);
        return customerPhoto;
    }
}
