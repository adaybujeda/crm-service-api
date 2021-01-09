package com.agilemonkeys.crm.storage;

import com.agilemonkeys.crm.domain.Customer;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomersDao {
    @SqlUpdate("INSERT INTO customers (customer_id, provided_id, name, surname, photo_id, version, created_date, created_by, updated_date, updated_by) " +
               "VALUES (:customerId, :providedId, :name, :surname, :photoId, :version, :createdDate, :createdBy, :updatedDate, :updatedBy)")
    public int insertCustomer(@BindBean Customer customer);

    @SqlUpdate("UPDATE customers SET provided_id=:providedId, name=:name, surname=:surname, photo_id=:photoId, version=:version, updated_date=:updatedDate, updated_by=:updatedBy " +
               "WHERE customer_id=:customerId AND version=:oldVersion")
    public int updateCustomer(@BindBean Customer customer, @Bind("oldVersion") Integer oldVersion);

    @SqlUpdate("DELETE FROM customers WHERE customer_id=:customerId")
    public int deleteCustomer(@Bind("customerId") UUID customerId);

    @SqlQuery("SELECT * FROM customers")
    public List<Customer> getCustomers();

    @SqlQuery("SELECT * FROM customers WHERE customer_id = :customerId")
    public Optional<Customer> getCustomerById(@Bind("customerId") UUID customerId);

    @SqlQuery("SELECT * FROM customers WHERE provided_id = :providedId")
    public Optional<Customer> getCustomerByProvidedId(@Bind("providedId") String providedId);
}
