package com.agilemonkeys.crm;

import com.agilemonkeys.crm.domain.User;
import com.agilemonkeys.crm.exceptions.NotFoundExceptionMapper;
import com.agilemonkeys.crm.exceptions.ValidationExceptionMapper;
import com.agilemonkeys.crm.resources.UsersResource;
import com.agilemonkeys.crm.services.CreateUserService;
import com.agilemonkeys.crm.services.GetUsersService;
import com.agilemonkeys.crm.storage.UsersDao;
import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.ResourceConfigurationSourceProvider;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.jdbi3.JdbiFactory;
import io.dropwizard.jersey.validation.JerseyViolationExceptionMapper;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.reflect.ConstructorMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrmServiceApiApplication extends Application<CrmServiceApiConfiguration> {

    private static final Logger log = LoggerFactory.getLogger(CrmServiceApiApplication.class);

    public static void main(final String[] args) throws Exception {
        new CrmServiceApiApplication().run(args);
    }

    @Override
    public String getName() {
        return "CrmServiceApi";
    }

    @Override
    public void initialize(final Bootstrap<CrmServiceApiConfiguration> bootstrap) {
        // LOAD PROPERTIES FROM JAR
        // LOAD ENV VARIABLES INTO PROPERTIES
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(new ResourceConfigurationSourceProvider(), new EnvironmentVariableSubstitutor(false))
        );

        bootstrap.addBundle(new CrmMigrationsBundle());
    }

    @Override
    public void run(final CrmServiceApiConfiguration config,
                    final Environment environment) {
        //CREATE DB CONNECTION POOL
        final JdbiFactory factory = new JdbiFactory();
        final Jdbi jdbi = factory.build(environment, config.getDataSource(), "crmDatabase");
        jdbi.registerRowMapper(ConstructorMapper.factory(User.class));
        UsersDao usersDao = jdbi.onDemand(UsersDao.class);
        log.info("action=db-init driver={} dbUrl={}", config.getDataSource().getDriverClass(), config.getDataSource().getUrl());

        //EXCEPTIONS
        environment.jersey().register(new ValidationExceptionMapper(new JerseyViolationExceptionMapper()));
        environment.jersey().register(new NotFoundExceptionMapper());

        //SERVICES
        CreateUserService createUserService = new CreateUserService(usersDao);
        GetUsersService getUsersService = new GetUsersService(usersDao);

        //RESOURCES
        environment.jersey().register(new UsersResource(createUserService, getUsersService));
    }

}
