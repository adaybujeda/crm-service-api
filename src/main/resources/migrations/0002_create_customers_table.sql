--liquibase formatted sql

--changeset adaybujeda:2
CREATE TABLE customers (
  customer_id CHAR(36) NOT NULL PRIMARY KEY,
  provided_id VARCHAR(50) NOT NULL,
  name VARCHAR(50) NOT NULL,
  surname VARCHAR(100) NOT NULL,
  version INT NOT NULL,
  created_date TIMESTAMP NOT NULL,
  created_by CHAR(36) NOT NULL,
  updated_date TIMESTAMP NOT NULL,
  updated_by CHAR(36) NOT NULL,

  CONSTRAINT customers_provided_id UNIQUE (provided_id)
);