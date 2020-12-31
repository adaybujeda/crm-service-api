package com.agilemonkeys.crm;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class CrmServiceApiConfiguration extends Configuration {

    @Valid
    @NotNull
    private DataSourceFactory dataSource = new DataSourceFactory();

    @JsonProperty("dataSource")
    public DataSourceFactory getDataSource() {
        return dataSource;
    }
}
