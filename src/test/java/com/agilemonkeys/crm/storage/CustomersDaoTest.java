package com.agilemonkeys.crm.storage;

import com.agilemonkeys.crm.RunningServiceBaseTest;
import com.agilemonkeys.crm.domain.Customer;
import com.agilemonkeys.crm.domain.CustomerBuilder;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.reflect.ConstructorMapper;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CustomersDaoTest extends RunningServiceBaseTest {

    private static CustomersDao underTest;

    @BeforeClass
    public static void beforeTest() throws Exception {
        Jdbi jdbi = Jdbi.create("jdbc:h2:mem:test", "sa", "sa");
        jdbi.installPlugin(new SqlObjectPlugin());
        jdbi.registerRowMapper(ConstructorMapper.factory(Customer.class));

        underTest = jdbi.onDemand(CustomersDao.class);
    }

    @Test
    public void should_store_customer_in_database() {
        insertCustomer(UUID.randomUUID());
    }

    @Test
    public void should_find_customer_by_customerId() {
        Customer newCustomer = insertCustomer(UUID.randomUUID());
        Optional<Customer> byId = underTest.getCustomerById(newCustomer.getCustomerId());
        MatcherAssert.assertThat(byId.isPresent(), Matchers.is(true));
        MatcherAssert.assertThat(byId.get(), Matchers.is(newCustomer));
    }

    @Test
    public void should_find_customer_by_providedId() {
        Customer newCustomer = insertCustomer(UUID.randomUUID());
        Optional<Customer> byProvidedId = underTest.getCustomerByProvidedId(newCustomer.getProvidedId());
        MatcherAssert.assertThat(byProvidedId.isPresent(), Matchers.is(true));
        MatcherAssert.assertThat(byProvidedId.get(), Matchers.is(newCustomer));
    }

    @Test
    public void should_get_all_customers_in_database() {
        insertCustomer(UUID.randomUUID());
        List<Customer> customers = underTest.getCustomers();
        MatcherAssert.assertThat(customers.isEmpty(), Matchers.is(false));
        MatcherAssert.assertThat(customers.size(), Matchers.greaterThan(0));
    }

    @Test
    public void should_update_customer_when_old_version_match() {
        Customer oldCustomer = insertCustomer(UUID.randomUUID(), 1);
        Customer updatedCustomer = CustomerBuilder.from(oldCustomer).withName("newName").withSurname("newSurname")
                .withVersion(2).build();
        int updatedRows = underTest.updateCustomer(updatedCustomer, oldCustomer.getVersion());
        MatcherAssert.assertThat(updatedRows, Matchers.is(1));

        Optional<Customer> byId = underTest.getCustomerById(oldCustomer.getCustomerId());
        MatcherAssert.assertThat(byId.isPresent(), Matchers.is(true));
        MatcherAssert.assertThat(byId.get(), Matchers.is(updatedCustomer));
    }

    @Test
    public void should_not_update_customer_when_old_version_does_not_match() {
        Customer oldCustomer = insertCustomer(UUID.randomUUID(), 2);
        Customer updatedCustomer = CustomerBuilder.from(oldCustomer).withName("newName").withSurname("newSurname")
                .withVersion(2).build();
        int updatedRows = underTest.updateCustomer(updatedCustomer, 1);
        MatcherAssert.assertThat(updatedRows, Matchers.is(0));
        MatcherAssert.assertThat(oldCustomer.getCustomerId(), Matchers.is(updatedCustomer.getCustomerId()));
    }

    @Test
    public void should_delete_customer() {
        Customer customer = insertCustomer(UUID.randomUUID());
        Optional<Customer> customerById = underTest.getCustomerById(customer.getCustomerId());
        MatcherAssert.assertThat(customerById.isPresent(), Matchers.is(true));

        int deletedUsers = underTest.deleteCustomer(customer.getCustomerId());
        MatcherAssert.assertThat(deletedUsers, Matchers.is(1));

        customerById = underTest.getCustomerById(customer.getCustomerId());
        MatcherAssert.assertThat(customerById.isPresent(), Matchers.is(false));
    }

    private Customer createCustomer(UUID customerId, Integer version) {
        String providedId = UUID.randomUUID().toString();
        String name = UUID.randomUUID().toString();
        String surname = UUID.randomUUID().toString();
        UUID createdUpdatedBy = UUID.randomUUID();
        Customer newCustomer = CustomerBuilder.builder().withCustomerId(customerId).withProvidedId(providedId)
                .withName(name).withSurname(surname).withVersion(version)
                .withCreatedBy(createdUpdatedBy).withUpdatedBy(createdUpdatedBy).build();
        return newCustomer;
    }

    private Customer insertCustomer(UUID customerId, Integer version) {
        Customer newCustomer = createCustomer(customerId, version);
        int rowsInserted = underTest.insertCustomer(newCustomer);
        MatcherAssert.assertThat(rowsInserted, CoreMatchers.is(1));
        return newCustomer;
    }

    private Customer insertCustomer(UUID customerId) {
        return insertCustomer(customerId, 1);
    }

}