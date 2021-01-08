package com.agilemonkeys.crm.services.customer;

import com.agilemonkeys.crm.storage.CustomersDao;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.UUID;

public class DeleteCustomerServiceTest {

    private static final UUID CUSTOMER_ID = UUID.randomUUID();

    private CustomersDao customersDao = Mockito.mock(CustomersDao.class);

    private DeleteCustomerService underTest = new DeleteCustomerService(customersDao);

    @Test
    public void should_return_true_when_customer_is_deleted_in_database() {
        Mockito.when(customersDao.deleteCustomer(CUSTOMER_ID)).thenReturn(1);
        boolean result = underTest.deleteCustomer(CUSTOMER_ID);

        MatcherAssert.assertThat(result, Matchers.is(true));
    }

    @Test
    public void should_return_false_when_customer_is_not_deleted_in_database() {
        Mockito.when(customersDao.deleteCustomer(CUSTOMER_ID)).thenReturn(0);
        boolean result = underTest.deleteCustomer(CUSTOMER_ID);

        MatcherAssert.assertThat(result, Matchers.is(false));
    }

}