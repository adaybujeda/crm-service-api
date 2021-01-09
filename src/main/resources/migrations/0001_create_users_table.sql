--liquibase formatted sql

--changeset adaybujeda:1
CREATE TABLE users (
  user_id CHAR(36) NOT NULL PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  username VARCHAR(255) NOT NULL,
  password_hash VARCHAR(512) NOT NULL,
  role VARCHAR(40) NOT NULL,
  version INT NOT NULL,
  created_date TIMESTAMP NOT NULL,
  updated_date TIMESTAMP NOT NULL,
  deleted_date TIMESTAMP,

  CONSTRAINT users_username UNIQUE (username)
);