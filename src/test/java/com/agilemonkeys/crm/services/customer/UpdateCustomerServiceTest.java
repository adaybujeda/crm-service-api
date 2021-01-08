package com.agilemonkeys.crm.services.customer;

import com.agilemonkeys.crm.domain.Customer;
import com.agilemonkeys.crm.domain.CustomerBuilder;
import com.agilemonkeys.crm.exceptions.CrmServiceApiDuplicatedException;
import com.agilemonkeys.crm.exceptions.CrmServiceApiNotFoundException;
import com.agilemonkeys.crm.exceptions.CrmServiceApiStaleStateException;
import com.agilemonkeys.crm.resources.customer.CreateUpdateCustomerRequest;
import com.agilemonkeys.crm.storage.CustomersDao;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.UUID;

public class UpdateCustomerServiceTest {
    private static final UUID CUSTOMER_ID = UUID.randomUUID();
    private static final UUID UPDATED_BY = UUID.randomUUID();
    private static final Customer EXISTING_CUSTOMER = CustomerBuilder.builder().withCustomerId(CUSTOMER_ID)
            .withCreatedBy(UUID.randomUUID()).withUpdatedBy(UUID.randomUUID()).build();

    private CustomersDao customersDao = Mockito.mock(CustomersDao.class);
    private DuplicatedIdService duplicatedIdService = Mockito.mock(DuplicatedIdService.class);

    private UpdateCustomerService underTest = new UpdateCustomerService(customersDao, duplicatedIdService);

    @Test(expected = CrmServiceApiNotFoundException.class)
    public void updateCustomer_should_throw_not_found_exception_when_customer_not_in_database() {
        Mockito.when(customersDao.getCustomerById(CUSTOMER_ID)).thenReturn(Optional.empty());
        underTest.updateCustomer(CUSTOMER_ID, 10, createRequest(), UPDATED_BY);
    }

    @Test(expected = CrmServiceApiStaleStateException.class)
    public void updateCustomer_should_throw_stale_state_exception_when_request_version_is_different_from_database() {
        Integer staleVersion = 10;
        Customer dbCustomer = CustomerBuilder.builder().withCustomerId(CUSTOMER_ID).withVersion(11).build();
        Mockito.when(customersDao.getCustomerById(CUSTOMER_ID)).thenReturn(Optional.of(dbCustomer));

        CreateUpdateCustomerRequest request = createRequest();
        underTest.updateCustomer(CUSTOMER_ID, staleVersion, request, UPDATED_BY);
    }

    @Test(expected = CrmServiceApiStaleStateException.class)
    public void updateCustomer_should_throw_stale_state_exception_when_database_update_returns_0() {
        Integer version = 10;
        Customer dbCustomer = CustomerBuilder.builder().withCustomerId(CUSTOMER_ID).withVersion(version).build();
        Mockito.when(customersDao.getCustomerById(CUSTOMER_ID)).thenReturn(Optional.of(dbCustomer));
        Mockito.when(customersDao.updateCustomer(Mockito.any(Customer.class), Mockito.eq(version))).thenReturn(0);

        CreateUpdateCustomerRequest request = createRequest();
        underTest.updateCustomer(CUSTOMER_ID, version, request, UPDATED_BY);
    }

    @Test(expected = CrmServiceApiDuplicatedException.class)
    public void updateCustomer_should_propagate_exception_when_duplicatedUserService_throws_one() {
        CreateUpdateCustomerRequest request = createRequest();
        Mockito.when(customersDao.getCustomerById(CUSTOMER_ID)).thenReturn(Optional.of(EXISTING_CUSTOMER));
        Mockito.doThrow(new CrmServiceApiDuplicatedException("test")).when(duplicatedIdService).checkProvidedId(request.getProvidedId(), Optional.of(CUSTOMER_ID));

        underTest.updateCustomer(CUSTOMER_ID, EXISTING_CUSTOMER.getVersion(), request, UPDATED_BY);
    }

    @Test
    public void updateCustomer_should_update_customer_when_version_matches() {
        CreateUpdateCustomerRequest request = createRequest();
        Mockito.when(customersDao.getCustomerById(CUSTOMER_ID)).thenReturn(Optional.of(EXISTING_CUSTOMER));
        Mockito.when(customersDao.updateCustomer(Mockito.any(Customer.class), Mockito.eq(EXISTING_CUSTOMER.getVersion()))).thenReturn(1);

        Customer result = underTest.updateCustomer(CUSTOMER_ID, EXISTING_CUSTOMER.getVersion(), request, UPDATED_BY);

        MatcherAssert.assertThat(result.getCustomerId(), Matchers.is(EXISTING_CUSTOMER.getCustomerId()));
        MatcherAssert.assertThat(result.getProvidedId(), Matchers.is(request.getProvidedId()));
        MatcherAssert.assertThat(result.getName(), Matchers.is(request.getName()));
        MatcherAssert.assertThat(result.getSurname(), Matchers.is(request.getSurname()));
        MatcherAssert.assertThat(result.getVersion(), Matchers.is(EXISTING_CUSTOMER.getVersion() + 1));
        MatcherAssert.assertThat(result.getCreatedDate(), Matchers.is(EXISTING_CUSTOMER.getCreatedDate()));
        MatcherAssert.assertThat(result.getCreatedBy(), Matchers.is(EXISTING_CUSTOMER.getCreatedBy()));
        MatcherAssert.assertThat(result.getUpdatedDate(), Matchers.not(Matchers.is(EXISTING_CUSTOMER.getUpdatedDate())));
        MatcherAssert.assertThat(result.getUpdatedBy(), Matchers.is(UPDATED_BY));

        Mockito.verify(customersDao).getCustomerById(CUSTOMER_ID);
        Mockito.verify(customersDao).updateCustomer(Mockito.any(Customer.class), Mockito.eq(EXISTING_CUSTOMER.getVersion()));
        Mockito.verify(duplicatedIdService).checkProvidedId(request.getProvidedId(), Optional.of(EXISTING_CUSTOMER.getCustomerId()));
    }

    private CreateUpdateCustomerRequest createRequest() {
        return new CreateUpdateCustomerRequest(UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString());
    }


}