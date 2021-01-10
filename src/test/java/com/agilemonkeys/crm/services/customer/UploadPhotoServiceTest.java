package com.agilemonkeys.crm.services.customer;

import com.agilemonkeys.crm.domain.Customer;
import com.agilemonkeys.crm.domain.CustomerBuilder;
import com.agilemonkeys.crm.domain.CustomerPhoto;
import com.agilemonkeys.crm.exceptions.CrmServiceApiDuplicatedException;
import com.agilemonkeys.crm.exceptions.CrmServiceApiStaleStateException;
import com.agilemonkeys.crm.storage.CustomerPhotosDao;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.UUID;

public class UploadPhotoServiceTest {

    private static final UUID CUSTOMER_ID = UUID.randomUUID();
    private static final UUID CREATED_BY = UUID.randomUUID();
    private static final Customer EXISTING_CUSTOMER = CustomerBuilder.builder().withCustomerId(CUSTOMER_ID)
            .withCreatedBy(UUID.randomUUID()).withUpdatedBy(UUID.randomUUID()).build();

    private final String TYPE = "image/jpeg";
    private final byte[] PHOTO = new byte[100];;

    private CustomerPhotosDao customerPhotosDao = Mockito.mock(CustomerPhotosDao.class);
    private CheckCustomerStateService checkCustomerStateService = Mockito.mock(CheckCustomerStateService.class);

    private UploadPhotoService underTest = new UploadPhotoService(customerPhotosDao, checkCustomerStateService);

    @Test(expected = CrmServiceApiDuplicatedException.class)
    public void should_propagate_exception_when_checkCustomerStateService_throws_one() {
        Integer version = 10;
        Mockito.when(checkCustomerStateService.checkCustomerState(CUSTOMER_ID, version)).thenThrow(new CrmServiceApiDuplicatedException("test"));

        underTest.createPhotoForCustomer(CUSTOMER_ID, version, TYPE, PHOTO, CREATED_BY);
    }

    @Test(expected = CrmServiceApiStaleStateException.class)
    public void should_propagate_exception_when_updateCustomerService_throws_one() {
        Integer version = 10;
        Mockito.when(checkCustomerStateService.checkCustomerState(CUSTOMER_ID, version)).thenReturn(EXISTING_CUSTOMER);
        Mockito.when(customerPhotosDao.updateCustomerWithNewPhoto(Mockito.any(Customer.class), Mockito.any(CustomerPhoto.class), Mockito.eq(version))).thenThrow(new CrmServiceApiStaleStateException("test"));

        underTest.createPhotoForCustomer(CUSTOMER_ID, version, TYPE, PHOTO, CREATED_BY);
    }

    @Test
    public void should_create_photo_when_customer_exists() {
        Integer version = 10;
        Mockito.when(checkCustomerStateService.checkCustomerState(CUSTOMER_ID, version)).thenReturn(EXISTING_CUSTOMER);
        Mockito.when(customerPhotosDao.updateCustomerWithNewPhoto(Mockito.any(Customer.class), Mockito.any(CustomerPhoto.class), Mockito.eq(version))).thenReturn(1);

        CustomerPhoto result = underTest.createPhotoForCustomer(CUSTOMER_ID, version, TYPE, PHOTO, CREATED_BY);

        checkCustomerPhoto(result);

        Mockito.verify(checkCustomerStateService).checkCustomerState(CUSTOMER_ID, version);
        ArgumentCaptor<CustomerPhoto> createdPhoto = ArgumentCaptor.forClass(CustomerPhoto.class);
        ArgumentCaptor<Customer> updatedCustomer = ArgumentCaptor.forClass(Customer.class);
        Mockito.verify(customerPhotosDao).updateCustomerWithNewPhoto(updatedCustomer.capture(), createdPhoto.capture(), Mockito.eq(version));

        checkCustomerPhoto(createdPhoto.getValue());
        checkCustomerUpdate(updatedCustomer.getValue());
    }

    private void checkCustomerPhoto(CustomerPhoto result) {
        MatcherAssert.assertThat(result.getCustomerId(), Matchers.is(EXISTING_CUSTOMER.getCustomerId()));
        MatcherAssert.assertThat(result.getContentType(), Matchers.is(TYPE));
        MatcherAssert.assertThat(result.getPhoto(), Matchers.is(PHOTO));
        MatcherAssert.assertThat(result.getCreatedDate(), Matchers.notNullValue());
    }

    private void checkCustomerUpdate(Customer result) {
        MatcherAssert.assertThat(result.getCustomerId(), Matchers.is(EXISTING_CUSTOMER.getCustomerId()));
        MatcherAssert.assertThat(result.getPhotoId(), Matchers.is(EXISTING_CUSTOMER.getCustomerId()));
        MatcherAssert.assertThat(result.getVersion(), Matchers.is(EXISTING_CUSTOMER.getVersion() + 1));
        MatcherAssert.assertThat(result.getUpdatedDate(), Matchers.not(Matchers.is(EXISTING_CUSTOMER.getUpdatedDate())));
        MatcherAssert.assertThat(result.getUpdatedBy(), Matchers.is(CREATED_BY));
    }
}