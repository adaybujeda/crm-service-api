package com.agilemonkeys.crm.storage;

import com.agilemonkeys.crm.RunningServiceBaseTest;
import com.agilemonkeys.crm.domain.Customer;
import com.agilemonkeys.crm.domain.CustomerPhoto;
import com.agilemonkeys.crm.util.CustomerFactory;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.reflect.ConstructorMapper;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public class CustomerPhotosDaoTest extends RunningServiceBaseTest {

    private static CustomersDao customerDao;

    private static CustomerPhotosDao underTest;

    @BeforeClass
    public static void beforeTest() throws Exception {
        Jdbi jdbi = Jdbi.create("jdbc:h2:mem:test", "sa", "sa");
        jdbi.installPlugin(new SqlObjectPlugin());
        jdbi.registerRowMapper(ConstructorMapper.factory(Customer.class));
        jdbi.registerRowMapper(ConstructorMapper.factory(CustomerPhoto.class));

        customerDao = jdbi.onDemand(CustomersDao.class);
        underTest = jdbi.onDemand(CustomerPhotosDao.class);
    }


    @Test
    public void should_store_photo_in_database() {
        insertCustomerPhoto(UUID.randomUUID());
    }

    @Test
    public void should_find_photo_by_customerId() {
        CustomerPhoto newPhoto = insertCustomerPhoto(UUID.randomUUID());
        Optional<CustomerPhoto> byId = underTest.getCustomerPhotoById(newPhoto.getCustomerId());
        MatcherAssert.assertThat(byId.isPresent(), Matchers.is(true));
        MatcherAssert.assertThat(byId.get().getCustomerId(), Matchers.is(newPhoto.getCustomerId()));
        MatcherAssert.assertThat(byId.get().getContentType(), Matchers.is(newPhoto.getContentType()));
        MatcherAssert.assertThat(byId.get().getCreatedDate(), Matchers.is(newPhoto.getCreatedDate()));
    }

    @Test
    public void should_delete_photo_when_deleting_customer() {
        CustomerPhoto newPhoto = insertCustomerPhoto(UUID.randomUUID());
        customerDao.deleteCustomer(newPhoto.getCustomerId());
        Optional<CustomerPhoto> byId = underTest.getCustomerPhotoById(newPhoto.getCustomerId());
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
        customerDao.insertCustomer(customer);

        CustomerPhoto newPhoto = createCustomerPhoto(customerId);
        int rowsInserted = underTest.insertCustomerPhoto(newPhoto);
        MatcherAssert.assertThat(rowsInserted, CoreMatchers.is(1));
        return newPhoto;
    }

}