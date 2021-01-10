package com.agilemonkeys.crm;

import com.agilemonkeys.crm.domain.Customer;
import com.agilemonkeys.crm.domain.CustomerPhoto;
import com.agilemonkeys.crm.domain.User;
import com.agilemonkeys.crm.storage.CustomerPhotosDao;
import com.agilemonkeys.crm.storage.CustomersDao;
import com.agilemonkeys.crm.storage.UUIDArgumentFactory;
import com.agilemonkeys.crm.storage.UsersDao;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.jdbi3.JdbiFactory;
import io.dropwizard.setup.Environment;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.reflect.ConstructorMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrmStorageFactory {

    private static final Logger log = LoggerFactory.getLogger(CrmStorageFactory.class);

    public CrmStorageContext init(Environment environment, DataSourceFactory config) {
        //CREATE DB CONNECTION POOL
        final JdbiFactory factory = new JdbiFactory();
        final Jdbi jdbi = factory.build(environment, config, "crmDatabase");
        jdbi.registerArgument(new UUIDArgumentFactory());
        jdbi.registerRowMapper(ConstructorMapper.factory(User.class));
        jdbi.registerRowMapper(ConstructorMapper.factory(Customer.class));
        jdbi.registerRowMapper(ConstructorMapper.factory(CustomerPhoto.class));
        UsersDao usersDao = jdbi.onDemand(UsersDao.class);
        CustomersDao customersDao = jdbi.onDemand(CustomersDao.class);
        CustomerPhotosDao customerPhotosDao = jdbi.onDemand(CustomerPhotosDao.class);
        log.info("action=db-init driver={} dbUrl={}", config.getDriverClass(), config.getUrl());

        return new CrmStorageContext(usersDao, customersDao, customerPhotosDao);
    }

    public static class CrmStorageContext {
        private UsersDao usersDao;
        private CustomersDao customersDao;
        private CustomerPhotosDao customerPhotosDao;

        public CrmStorageContext(UsersDao usersDao, CustomersDao customersDao, CustomerPhotosDao customerPhotosDao) {
            this.usersDao = usersDao;
            this.customersDao = customersDao;
            this.customerPhotosDao = customerPhotosDao;
        }

        public UsersDao getUsersDao() {
            return usersDao;
        }

        public CustomerPhotosDao getCustomerPhotosDao() {
            return customerPhotosDao;
        }

        public CustomersDao getCustomersDao() {
            return customersDao;
        }
    }
}
