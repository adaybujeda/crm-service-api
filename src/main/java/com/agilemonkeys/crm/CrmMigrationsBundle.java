package com.agilemonkeys.crm;

import com.agilemonkeys.crm.config.CrmServiceApiConfiguration;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.migrations.MigrationsBundle;

public class CrmMigrationsBundle extends MigrationsBundle<CrmServiceApiConfiguration> {
    @Override
    public PooledDataSourceFactory getDataSourceFactory(CrmServiceApiConfiguration configuration) {
        return configuration.getDataSource();
    }
}
