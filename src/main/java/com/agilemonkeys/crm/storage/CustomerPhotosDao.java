package com.agilemonkeys.crm.storage;

import com.agilemonkeys.crm.domain.Customer;
import com.agilemonkeys.crm.domain.CustomerPhoto;
import com.agilemonkeys.crm.exceptions.CrmServiceApiStaleStateException;
import org.jdbi.v3.sqlobject.CreateSqlObject;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.transaction.Transaction;

import java.util.Optional;
import java.util.UUID;

public interface CustomerPhotosDao {
    @CreateSqlObject
    public CustomersDao getCustomerDao();

    @SqlUpdate("INSERT INTO customer_photos (customer_id, content_type, photo, created_date) " +
               "VALUES (:customerId, :contentType, :photo, :createdDate)")
    public int insertCustomerPhoto(@BindBean CustomerPhoto customerPhoto);

    @SqlUpdate("UPDATE customer_photos SET content_type=:contentType, photo=:photo, created_date=:createdDate " +
               "WHERE customer_id=:customerId")
    public int updateCustomer(@BindBean CustomerPhoto customerPhoto);

    @SqlQuery("SELECT * FROM customer_photos WHERE customer_id = :customerId")
    public Optional<CustomerPhoto> getCustomerPhoto(@Bind("customerId") UUID customerId);

    @Transaction
    public default int updateCustomerWithNewPhoto(Customer customer, CustomerPhoto customerPhoto, Integer oldVersion) {
        if(getCustomerPhoto(customer.getCustomerId()).isEmpty()) {
            insertCustomerPhoto(customerPhoto);
        } else {
            updateCustomer(customerPhoto);
        }
        int updateResult = getCustomerDao().updateCustomer(customer, oldVersion);
        if(updateResult != 1) {
            //ROLLBACK TRANSACTION
            throw new CrmServiceApiStaleStateException(String.format("Concurrent modification. CustomerId: %s modified since request was made. Try again", customer.getCustomerId()));
        }

        return updateResult;
    }

}
