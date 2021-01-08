--liquibase formatted sql

--changeset adaybujeda:3
CREATE TABLE customer_photos(
  customer_id CHAR(36) NOT NULL PRIMARY KEY,
  content_type VARCHAR(50) NOT NULL,
  --10MB
  photo VARBINARY(10000000) NOT NULL,
  created_date TIMESTAMP NOT NULL,

  CONSTRAINT fk_photos_customer_id FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
  ON UPDATE CASCADE
  ON DELETE CASCADE
);
