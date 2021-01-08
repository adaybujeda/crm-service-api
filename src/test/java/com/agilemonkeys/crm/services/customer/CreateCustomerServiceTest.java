package com.agilemonkeys.crm.services.customer;

import com.agilemonkeys.crm.domain.Customer;
import com.agilemonkeys.crm.exceptions.CrmServiceApiDuplicatedException;
import com.agilemonkeys.crm.resources.customer.CreateUpdateCustomerRequest;
import com.agilemonkeys.crm.storage.CustomersDao;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.UUID;

public class CreateCustomerServiceTest {

    private static final UUID CREATED_BY = UUID.randomUUID();

    private CustomersDao customersDao = Mockito.mock(CustomersDao.class);
    private DuplicatedIdService duplicatedIdService = Mockito.mock(DuplicatedIdService.class);

    private CreateCustomerService underTest = new CreateCustomerService(customersDao, duplicatedIdService);

    @Test(expected = CrmServiceApiDuplicatedException.class)
    public void should_propagate_exception_when_duplicatedIdService_throws_one() {
        CreateUpdateCustomerRequest request = new CreateUpdateCustomerRequest("some-id", "name", "surname");
        Mockito.doThrow(new CrmServiceApiDuplicatedException("test")).when(duplicatedIdService).checkProvidedId(request.getProvidedId(), Optional.empty());

        underTest.createCustomer(CREATED_BY, request);
    }

    @Test
    public void should_create_customer_from_request_and_populate_default_fields() {
        CreateUpdateCustomerRequest request = new CreateUpdateCustomerRequest("some-id", "name", "surname");

        Customer result = underTest.createCustomer(CREATED_BY, request);

        MatcherAssert.assertThat(result.getProvidedId(), Matchers.is(request.getProvidedId()));
        MatcherAssert.assertThat(result.getName(), Matchers.is(request.getName()));
        MatcherAssert.assertThat(result.getSurname(), Matchers.is(request.getSurname()));

        MatcherAssert.assertThat(result.getCustomerId(), Matchers.notNullValue());
        MatcherAssert.assertThat(result.getVersion(), Matchers.is(1));
        MatcherAssert.assertThat(result.getCreatedDate(), Matchers.notNullValue());
        MatcherAssert.assertThat(result.getCreatedBy(), Matchers.is(CREATED_BY));
        MatcherAssert.assertThat(result.getUpdatedDate(), Matchers.notNullValue());
        MatcherAssert.assertThat(result.getUpdatedBy(), Matchers.is(CREATED_BY));

        Mockito.verify(customersDao).insertCustomer(Mockito.any(Customer.class));
        Mockito.verify(duplicatedIdService).checkProvidedId(request.getProvidedId(), Optional.empty());
    }

}