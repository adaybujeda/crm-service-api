package com.agilemonkeys.crm.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

import javax.validation.Valid;

public class CrmServiceApiConfiguration extends Configuration {

    @Valid
    private CrmJwtConfig jwtConfig = new CrmJwtConfig();

    @Valid
    private DataSourceFactory dataSource = new DataSourceFactory();

    @JsonProperty("jwtConfig")
    public CrmJwtConfig getJwtConfig() {
        return jwtConfig;
    }

    @JsonProperty("dataSource")
    public DataSourceFactory getDataSource() {
        return dataSource;
    }
}
