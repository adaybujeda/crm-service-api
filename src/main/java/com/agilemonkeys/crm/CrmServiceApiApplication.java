package com.agilemonkeys.crm;

import com.agilemonkeys.crm.resources.UsersResource;
import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.ResourceConfigurationSourceProvider;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.jdbi3.JdbiFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.jdbi.v3.core.Jdbi;

public class CrmServiceApiApplication extends Application<CrmServiceApiConfiguration> {

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
        // TODO: implement application
        //CREATE DB CONNECTION POOL
        final JdbiFactory factory = new JdbiFactory();
        final Jdbi jdbi = factory.build(environment, config.getDataSource(), "crmDatabase");

        environment.jersey().register(new UsersResource());
    }

}
