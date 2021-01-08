package com.agilemonkeys.crm.services.customer;

import com.agilemonkeys.crm.domain.Customer;
import com.agilemonkeys.crm.domain.CustomerBuilder;
import com.agilemonkeys.crm.domain.CustomerPhoto;
import com.agilemonkeys.crm.exceptions.CrmServiceApiNotFoundException;
import com.agilemonkeys.crm.storage.CustomerPhotosDao;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public class GetCustomerPhotoServiceTest {

    private static final UUID CUSTOMER_ID = UUID.randomUUID();
    private static final UUID CREATED_BY = UUID.randomUUID();
    private static final Customer EXISTING_CUSTOMER = CustomerBuilder.builder().withCustomerId(CUSTOMER_ID)
            .withCreatedBy(UUID.randomUUID()).withUpdatedBy(UUID.randomUUID()).build();

    private final String TYPE = "image/jpeg";
    private final byte[] PHOTO = new byte[100];;

    private CustomerPhotosDao customerPhotosDao = Mockito.mock(CustomerPhotosDao.class);

    private GetCustomerPhotoService underTest = new GetCustomerPhotoService(customerPhotosDao);

    @Test(expected = CrmServiceApiNotFoundException.class)
    public void should_throw_not_found_exception_when_no_customer_photo() {
        Mockito.when(customerPhotosDao.getCustomerPhoto(CUSTOMER_ID)).thenReturn(Optional.empty());
        underTest.getCustomerPhoto(CUSTOMER_ID);
    }

    @Test
    public void should_return_customer_photo_when_found() {
        CustomerPhoto photo = new CustomerPhoto(CUSTOMER_ID, "image/png", new byte[1], LocalDateTime.now());
        Mockito.when(customerPhotosDao.getCustomerPhoto(CUSTOMER_ID)).thenReturn(Optional.of(photo));
        CustomerPhoto result = underTest.getCustomerPhoto(CUSTOMER_ID);

        MatcherAssert.assertThat(result.getCustomerId(), Matchers.is(photo.getCustomerId()));
        MatcherAssert.assertThat(result.getContentType(), Matchers.is(photo.getContentType()));
        MatcherAssert.assertThat(result.getCreatedDate(), Matchers.is(photo.getCreatedDate()));
        Mockito.verify(customerPhotosDao).getCustomerPhoto(CUSTOMER_ID);
    }
}