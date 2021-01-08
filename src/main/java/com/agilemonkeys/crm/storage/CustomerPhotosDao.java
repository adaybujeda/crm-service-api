package com.agilemonkeys.crm.storage;

import com.agilemonkeys.crm.domain.CustomerPhoto;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.Optional;
import java.util.UUID;

public interface CustomerPhotosDao {
    @SqlUpdate("INSERT INTO customer_photos (customer_id, content_type, photo, created_date) " +
               "VALUES (:customerId, :contentType, :photo, :createdDate)")
    public int insertCustomerPhoto(@BindBean CustomerPhoto customerPhoto);

    @SqlUpdate("UPDATE customer_photos SET content_type=:contentType, photo=:photo, created_date=:createdDate " +
               "WHERE customer_id=:customerId")
    public int updateCustomerPhoto(@BindBean CustomerPhoto customerPhoto);

    @SqlQuery("SELECT * FROM customer_photos WHERE customer_id = :customerId")
    public Optional<CustomerPhoto> getCustomerPhoto(@Bind("customerId") UUID customerId);

}
