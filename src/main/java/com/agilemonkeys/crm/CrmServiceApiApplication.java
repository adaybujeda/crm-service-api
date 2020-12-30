package com.agilemonkeys.crm;

import com.agilemonkeys.crm.resources.UsersResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

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
        // TODO: application initialization
    }

    @Override
    public void run(final CrmServiceApiConfiguration configuration,
                    final Environment environment) {
        // TODO: implement application
        environment.jersey().register(new UsersResource());
    }

}
