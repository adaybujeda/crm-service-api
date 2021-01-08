package com.agilemonkeys.crm.services.customer;

import com.agilemonkeys.crm.domain.CustomerPhoto;
import com.agilemonkeys.crm.storage.CustomerPhotosDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.UUID;

public class UploadPhotoService {

    private static final Logger log = LoggerFactory.getLogger(UploadPhotoService.class);

    private final CustomerPhotosDao customerPhotosDao;

    public UploadPhotoService(CustomerPhotosDao customerPhotosDao) {
        this.customerPhotosDao = customerPhotosDao;
    }

    public CustomerPhoto createPhotoForCustomer(UUID customerId, String fileType, byte[] photoData) {
        CustomerPhoto customerPhoto = new CustomerPhoto(customerId, fileType, photoData, LocalDateTime.now());
        customerPhotosDao.insertCustomerPhoto(customerPhoto);
        log.info("action=createImage result=success customerId={} customerPhoto={}", customerId, customerPhoto);
        return customerPhoto;
    }
}
