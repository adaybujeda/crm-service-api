package com.agilemonkeys.crm;

import com.agilemonkeys.crm.domain.User;
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
        jdbi.registerRowMapper(ConstructorMapper.factory(User.class));
        UsersDao usersDao = jdbi.onDemand(UsersDao.class);
        log.info("action=db-init driver={} dbUrl={}", config.getDriverClass(), config.getUrl());

        return new CrmStorageContext(usersDao);
    }

    public static class CrmStorageContext {
        private UsersDao usersDao;

        public CrmStorageContext(UsersDao usersDao) {
            this.usersDao = usersDao;
        }

        public UsersDao getUsersDao() {
            return usersDao;
        }
    }
}
