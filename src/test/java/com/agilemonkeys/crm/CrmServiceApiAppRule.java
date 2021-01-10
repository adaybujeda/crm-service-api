package com.agilemonkeys.crm;

import com.agilemonkeys.crm.config.CrmServiceApiConfiguration;
import io.dropwizard.Application;
import io.dropwizard.testing.ConfigOverride;
import io.dropwizard.testing.junit.DropwizardAppRule;

import javax.annotation.Nullable;
import javax.ws.rs.client.Client;
import java.util.concurrent.TimeUnit;

public class CrmServiceApiAppRule extends DropwizardAppRule<CrmServiceApiConfiguration> {

    public CrmServiceApiAppRule(Class<? extends Application<CrmServiceApiConfiguration>> applicationClass, @Nullable String configPath, ConfigOverride... configOverrides) {
        super(applicationClass, configPath, configOverrides);
    }

    @Override
    public Client client () {
        return super.clientBuilder().connectTimeout(5, TimeUnit.SECONDS).readTimeout(10, TimeUnit.SECONDS).build();
    }

}
