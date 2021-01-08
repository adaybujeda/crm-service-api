package com.agilemonkeys.crm.services.customer;

import com.agilemonkeys.crm.domain.Customer;
import com.agilemonkeys.crm.domain.CustomerBuilder;
import com.agilemonkeys.crm.exceptions.CrmServiceApiNotFoundException;
import com.agilemonkeys.crm.storage.CustomersDao;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class GetCustomersServiceTest {

    private CustomersDao customersDao = Mockito.mock(CustomersDao.class);

    private GetCustomersService underTest = new GetCustomersService(customersDao);

    @Test(expected = CrmServiceApiNotFoundException.class)
    public void getCustomerById_should_throw_not_found_exception_when_not_in_database() {
        UUID notFoundId = UUID.randomUUID();
        Mockito.when(customersDao.getCustomerById(notFoundId)).thenReturn(Optional.empty());

        underTest.getCustomerById(notFoundId);
    }

    @Test
    public void getCustomerById_should_return_customer_when_found_in_database() {
        UUID customerId = UUID.randomUUID();
        Customer customer = CustomerBuilder.builder().withCustomerId(customerId).build();
        Mockito.when(customersDao.getCustomerById(customerId)).thenReturn(Optional.of(customer));

        Customer result = underTest.getCustomerById(customerId);
        MatcherAssert.assertThat(result, Matchers.is(customer));
    }

    @Test
    public void getCustomers_should_return_all_customer_from_database() {
        UUID customerId = UUID.randomUUID();
        List<Customer> allCustomers = Arrays.asList(CustomerBuilder.builder().withCustomerId(UUID.randomUUID()).build(),
                CustomerBuilder.builder().withCustomerId(UUID.randomUUID()).build());
        Mockito.when(customersDao.getCustomers()).thenReturn(allCustomers);

        List<Customer> result = underTest.getAllCustomers();
        MatcherAssert.assertThat(result, Matchers.is(allCustomers));
    }

}