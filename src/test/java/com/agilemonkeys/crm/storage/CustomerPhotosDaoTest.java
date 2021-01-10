package com.agilemonkeys.crm.storage;

import com.agilemonkeys.crm.RunningServiceBaseTest;
import com.agilemonkeys.crm.domain.Customer;
import com.agilemonkeys.crm.domain.CustomerPhoto;
import com.agilemonkeys.crm.exceptions.CrmServiceApiStaleStateException;
import com.agilemonkeys.crm.util.CustomerFactory;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.reflect.ConstructorMapper;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public class CustomerPhotosDaoTest extends RunningServiceBaseTest {

    private static CustomerPhotosDao underTest;

    @BeforeClass
    public static void beforeTest() throws Exception {
        Jdbi jdbi = Jdbi.create(getDataSource().getUrl(), getDataSource().getUser(), getDataSource().getPassword());
        jdbi.installPlugin(new SqlObjectPlugin());
        jdbi.registerArgument(new UUIDArgumentFactory());
        jdbi.registerRowMapper(ConstructorMapper.factory(Customer.class));
        jdbi.registerRowMapper(ConstructorMapper.factory(CustomerPhoto.class));

        underTest = jdbi.onDemand(CustomerPhotosDao.class);
    }


    @Test
    public void should_store_photo_in_database() {
        insertCustomerPhoto(UUID.randomUUID());
    }

    @Test
    public void should_find_photo_by_customerId() {
        CustomerPhoto newPhoto = insertCustomerPhoto(UUID.randomUUID());
        Optional<CustomerPhoto> byId = underTest.getCustomerPhoto(newPhoto.getCustomerId());
        MatcherAssert.assertThat(byId.isPresent(), Matchers.is(true));
        MatcherAssert.assertThat(byId.get().getCustomerId(), Matchers.is(newPhoto.getCustomerId()));
        MatcherAssert.assertThat(byId.get().getContentType(), Matchers.is(newPhoto.getContentType()));
        MatcherAssert.assertThat(byId.get().getCreatedDate(), Matchers.is(newPhoto.getCreatedDate()));
    }

    @Test
    public void should_update_photo() {
        CustomerPhoto existingPhoto = insertCustomerPhoto(UUID.randomUUID());
        String newType = "updated";
        byte[] newBytes = new byte[10];
        LocalDateTime createdDate = LocalDateTime.now();
        CustomerPhoto updatedPhoto = new CustomerPhoto(existingPhoto.getCustomerId(), newType, newBytes, createdDate);
        int updated = underTest.updateCustomer(updatedPhoto);
        MatcherAssert.assertThat(updated, Matchers.is(1));

        Optional<CustomerPhoto> result = underTest.getCustomerPhoto(existingPhoto.getCustomerId());
        MatcherAssert.assertThat(result.isPresent(), Matchers.is(true));
        MatcherAssert.assertThat(result.get().getCustomerId(), Matchers.is(updatedPhoto.getCustomerId()));
        MatcherAssert.assertThat(result.get().getContentType(), Matchers.is(updatedPhoto.getContentType()));
        MatcherAssert.assertThat(result.get().getCreatedDate(), Matchers.is(updatedPhoto.getCreatedDate()));
    }

    @Test
    public void should_not_create_photo_when_customer_update_fails() {
        Customer customer = CustomerFactory.valid(UUID.randomUUID());
        underTest.getCustomerDao().insertCustomer(customer);
        MatcherAssert.assertThat(underTest.getCustomerPhoto(customer.getCustomerId()).isPresent(), Matchers.is(false));

        CustomerPhoto newPhoto = createCustomerPhoto(customer.getCustomerId());
        try {
            underTest.updateCustomerWithNewPhoto(customer, newPhoto, 10);
            Assert.fail();
        } catch (CrmServiceApiStaleStateException e) {

        }
        MatcherAssert.assertThat(underTest.getCustomerPhoto(customer.getCustomerId()).isPresent(), Matchers.is(false));
        Customer dbCustomer = underTest.getCustomerDao().getCustomerById(customer.getCustomerId()).get();
        MatcherAssert.assertThat(dbCustomer.getVersion(), Matchers.is(customer.getVersion()));
    }

    @Test
    public void should_delete_photo_when_deleting_customer() {
        CustomerPhoto newPhoto = insertCustomerPhoto(UUID.randomUUID());
        underTest.getCustomerDao().deleteCustomer(newPhoto.getCustomerId());
        Optional<CustomerPhoto> byId = underTest.getCustomerPhoto(newPhoto.getCustomerId());
        MatcherAssert.assertThat(byId.isPresent(), Matchers.is(false));
    }

    private CustomerPhoto createCustomerPhoto(UUID customerId) {
        String type = UUID.randomUUID().toString();
        byte[] photo = new byte[10000];
        CustomerPhoto newPhoto = new CustomerPhoto(customerId, type, photo, LocalDateTime.now());
        return newPhoto;
    }

    private CustomerPhoto insertCustomerPhoto(UUID customerId) {
        Customer customer = CustomerFactory.valid(customerId);
        underTest.getCustomerDao().insertCustomer(customer);

        CustomerPhoto newPhoto = createCustomerPhoto(customerId);
        int rowsInserted = underTest.insertCustomerPhoto(newPhoto);
        MatcherAssert.assertThat(rowsInserted, CoreMatchers.is(1));
        return newPhoto;
    }

}