package com.agilemonkeys.crm;

import com.agilemonkeys.crm.CrmAuthFactory.CrmAuthContext;
import com.agilemonkeys.crm.CrmStorageFactory.CrmStorageContext;
import com.agilemonkeys.crm.config.CrmServiceApiConfiguration;
import com.agilemonkeys.crm.exceptions.CrmServiceApiExceptionMapper;
import com.agilemonkeys.crm.exceptions.ValidationExceptionMapper;
import com.agilemonkeys.crm.resources.user.CreateUserResource;
import com.agilemonkeys.crm.resources.user.DeleteUserResource;
import com.agilemonkeys.crm.resources.user.GetUsersResource;
import com.agilemonkeys.crm.resources.user.UpdateUserResource;
import com.agilemonkeys.crm.resources.auth.LoginResource;
import com.agilemonkeys.crm.resources.auth.ResetPasswordResource;
import com.agilemonkeys.crm.services.auth.CrmPasswordHashService;
import com.agilemonkeys.crm.services.auth.LoginService;
import com.agilemonkeys.crm.services.auth.ResetPasswordService;
import com.agilemonkeys.crm.services.user.*;
import com.agilemonkeys.crm.storage.UsersDao;
import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.ResourceConfigurationSourceProvider;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.jersey.validation.JerseyViolationExceptionMapper;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
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
        bootstrap.addCommand(new CreateAdminUserCommand());
    }

    @Override
    public void run(final CrmServiceApiConfiguration config, final Environment environment) {
        CrmStorageContext storageContext = new CrmStorageFactory().init(environment, config.getDataSource());
        UsersDao usersDao = storageContext.getUsersDao();
        //AUTH SETUP
        CrmAuthContext authContext = new CrmAuthFactory().init(config, environment);

        //EXCEPTIONS
        environment.jersey().register(new ValidationExceptionMapper(new JerseyViolationExceptionMapper()));
        environment.jersey().register(new CrmServiceApiExceptionMapper());

        //SERVICES
        CrmPasswordHashService passwordHashService = new CrmPasswordHashService();
        DuplicatedUserService duplicatedUserService = new DuplicatedUserService(usersDao);
        GetUsersService getUsersService = new GetUsersService(usersDao);
        CreateUserService createUserService = new CreateUserService(usersDao, duplicatedUserService, passwordHashService);
        UpdateUserService updateUserService = new UpdateUserService(usersDao, duplicatedUserService);
        DeleteUserService deleteUserService = new DeleteUserService(usersDao);

        LoginService loginService = new LoginService(getUsersService, authContext.getJwtTokenFactory(), passwordHashService);
        ResetPasswordService resetPasswordService = new ResetPasswordService(getUsersService, updateUserService, passwordHashService);

        //RESOURCES
        environment.jersey().register(new LoginResource(loginService));
        environment.jersey().register(new ResetPasswordResource(resetPasswordService));
        environment.jersey().register(new GetUsersResource(getUsersService));
        environment.jersey().register(new CreateUserResource(createUserService));
        environment.jersey().register(new UpdateUserResource(updateUserService));
        environment.jersey().register(new DeleteUserResource(deleteUserService));
    }

}
