package com.agilemonkeys.crm;

import com.agilemonkeys.crm.config.CrmServiceApiConfiguration;
import com.agilemonkeys.crm.domain.User;
import com.agilemonkeys.crm.domain.UserRole;
import com.agilemonkeys.crm.exceptions.CrmServiceApiDuplicatedException;
import com.agilemonkeys.crm.resources.user.CreateUserRequest;
import com.agilemonkeys.crm.services.user.CreateUserService;
import com.agilemonkeys.crm.services.user.DuplicatedUserService;
import com.agilemonkeys.crm.services.auth.CrmPasswordHashService;
import io.dropwizard.cli.ConfiguredCommand;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateAdminUserCommand extends ConfiguredCommand<CrmServiceApiConfiguration> {

    private static final Logger log = LoggerFactory.getLogger(CreateAdminUserCommand.class);

    protected CreateAdminUserCommand() {
        super("create-admin-user", "Creates an ADMIN user");
    }

    @Override
    protected void run(Bootstrap<CrmServiceApiConfiguration> bootstrap, Namespace namespace, CrmServiceApiConfiguration config) throws Exception {
        Environment environment = new Environment(bootstrap.getApplication().getName(), bootstrap.getObjectMapper(), bootstrap.getValidatorFactory(), bootstrap.getMetricRegistry(), bootstrap.getClassLoader(), bootstrap.getHealthCheckRegistry(), config);
        bootstrap.run(config, environment);

        CrmStorageFactory.CrmStorageContext storageContext = new CrmStorageFactory().init(environment, config.getDataSource());
        CrmPasswordHashService passwordHashService = new CrmPasswordHashService();
        DuplicatedUserService duplicatedUserService = new DuplicatedUserService(storageContext.getUsersDao());
        CreateUserService createUserService = new CreateUserService(storageContext.getUsersDao(), duplicatedUserService, passwordHashService);

        String password = namespace.get("password");
        CreateUserRequest request = new CreateUserRequest("Admin User", "admin", password.trim(), UserRole.ADMIN);
        try {
            User adminUser = createUserService.createUser(request);
            log.info("action=create-admin-user completed adminUser={}", adminUser);
        } catch (CrmServiceApiDuplicatedException e) {
            log.info("action=create-admin-user result=admin-user-already-created message={}", e.getMessage());
        }

    }


    @Override
    public void configure(Subparser subparser) {
        super.configure(subparser);
        subparser.addArgument("-p", "--password")
                .dest("password")
                .type(String.class)
                .required(true)
                .help("The ADMIN user password");
    }
}
