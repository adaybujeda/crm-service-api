package com.agilemonkeys.crm.services.customer;

import com.agilemonkeys.crm.domain.Customer;
import com.agilemonkeys.crm.domain.CustomerBuilder;
import com.agilemonkeys.crm.exceptions.CrmServiceApiDuplicatedException;
import com.agilemonkeys.crm.storage.CustomersDao;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.UUID;

public class DuplicatedIdServiceTest {
    private static final UUID CUSTOMER_ID = UUID.randomUUID();
    private static final Customer EXISTING_CUSTOMER = CustomerBuilder.builder().withCustomerId(CUSTOMER_ID).build();
    private static final String PROVIDED_ID = "some-id";

    private CustomersDao customersDao = Mockito.mock(CustomersDao.class);
    private DuplicatedIdService underTest = new DuplicatedIdService(customersDao);

    @Test(expected = CrmServiceApiDuplicatedException.class)
    public void should_throw_duplicated_exception_when_request_providedId_already_in_database() {
        Mockito.when(customersDao.getCustomerByProvidedId(PROVIDED_ID)).thenReturn(Optional.of(EXISTING_CUSTOMER));

        underTest.checkProvidedId(PROVIDED_ID, Optional.empty());
    }

    @Test(expected = CrmServiceApiDuplicatedException.class)
    public void should_throw_duplicated_exception_when_request_username_already_in_database_for_another_user() {
        Mockito.when(customersDao.getCustomerByProvidedId(PROVIDED_ID)).thenReturn(Optional.of(EXISTING_CUSTOMER));

        UUID newCustomerId = UUID.randomUUID();
        underTest.checkProvidedId(PROVIDED_ID, Optional.of(newCustomerId));
    }

    @Test
    public void should_not_throw_duplicated_exception_when_request_username_not_in_database() {
        Mockito.when(customersDao.getCustomerByProvidedId(PROVIDED_ID)).thenReturn(Optional.empty());

        underTest.checkProvidedId(PROVIDED_ID, Optional.empty());
        Mockito.verify(customersDao).getCustomerByProvidedId(PROVIDED_ID);
    }

    @Test
    public void should_not_throw_duplicated_exception_when_request_username_not_in_database_passing_userId() {
        Mockito.when(customersDao.getCustomerByProvidedId(PROVIDED_ID)).thenReturn(Optional.empty());

        underTest.checkProvidedId(PROVIDED_ID, Optional.of(CUSTOMER_ID));

        Mockito.verify(customersDao).getCustomerByProvidedId(PROVIDED_ID);
    }

    @Test
    public void should_not_throw_duplicated_exception_when_request_username_already_in_database_for_the_same_user() {
        Mockito.when(customersDao.getCustomerByProvidedId(PROVIDED_ID)).thenReturn(Optional.of(EXISTING_CUSTOMER));

        underTest.checkProvidedId(PROVIDED_ID, Optional.of(CUSTOMER_ID));

        Mockito.verify(customersDao).getCustomerByProvidedId(PROVIDED_ID);
    }
}