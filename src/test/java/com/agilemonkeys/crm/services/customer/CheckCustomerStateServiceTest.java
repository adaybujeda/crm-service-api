package com.agilemonkeys.crm.services.customer;

import com.agilemonkeys.crm.domain.Customer;
import com.agilemonkeys.crm.domain.CustomerBuilder;
import com.agilemonkeys.crm.exceptions.CrmServiceApiNotFoundException;
import com.agilemonkeys.crm.exceptions.CrmServiceApiStaleStateException;
import com.agilemonkeys.crm.storage.CustomersDao;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.UUID;

public class CheckCustomerStateServiceTest {

    private static final UUID CUSTOMER_ID = UUID.randomUUID();

    private CustomersDao customersDao = Mockito.mock(CustomersDao.class);

    private CheckCustomerStateService underTest = new CheckCustomerStateService(customersDao);


    @Test(expected = CrmServiceApiNotFoundException.class)
    public void should_throw_not_found_exception_when_customer_not_in_database() {
        Mockito.when(customersDao.getCustomerById(CUSTOMER_ID)).thenReturn(Optional.empty());
        underTest.checkCustomerState(CUSTOMER_ID, 10);
    }

    @Test(expected = CrmServiceApiStaleStateException.class)
    public void should_throw_stale_state_exception_when_request_version_is_different_from_database() {
        Integer staleVersion = 10;
        Customer dbCustomer = CustomerBuilder.builder().withCustomerId(CUSTOMER_ID).withVersion(11).build();
        Mockito.when(customersDao.getCustomerById(CUSTOMER_ID)).thenReturn(Optional.of(dbCustomer));

        underTest.checkCustomerState(CUSTOMER_ID, staleVersion);
    }

    @Test
    public void should_return_customer_when_version_matches() {
        Customer dbCustomer = CustomerBuilder.builder().withCustomerId(CUSTOMER_ID).withVersion(11).build();
        Mockito.when(customersDao.getCustomerById(CUSTOMER_ID)).thenReturn(Optional.of(dbCustomer));

        Customer result = underTest.checkCustomerState(CUSTOMER_ID, dbCustomer.getVersion());

        MatcherAssert.assertThat(result, Matchers.is(dbCustomer));
        Mockito.verify(customersDao).getCustomerById(CUSTOMER_ID);
    }

}