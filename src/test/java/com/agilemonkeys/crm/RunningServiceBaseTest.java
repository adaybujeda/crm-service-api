package com.agilemonkeys.crm;

import com.agilemonkeys.crm.util.WithAuth;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.testing.ConfigOverride;
import org.junit.BeforeClass;
import org.junit.ClassRule;

import javax.ws.rs.client.Client;

public abstract class RunningServiceBaseTest implements WithAuth {

    @ClassRule
    public static final CrmServiceApiAppRule RULE =
            new CrmServiceApiAppRule(CrmServiceApiApplication.class, "/config.yml",
                    ConfigOverride.config("server.applicationConnectors[0].port", "0"),
                    ConfigOverride.config("server.adminConnectors[0].port", "0"));

    @BeforeClass
    public static void runMigrations() throws Exception {
        RULE.getApplication().run("db", "migrate", "/config.yml");
        RULE.getApplication().run("create-admin-user", "-p " + WithAuth.ADMIN_PASSWORD, "/config.yml");
    }

    @Override
    public RunningServiceBaseTest getRunningService() {
        return this;
    }

    public static Client getClient() {
        Client client = RULE.client();
        return client;
    }

    public static DataSourceFactory getDataSource() {
        return RULE.getConfiguration().getDataSource();
    }

    public static String getResourceUrl(String path) {
        return  String.format("http://localhost:%d%s", RULE.getLocalPort(), path);
    }

}
