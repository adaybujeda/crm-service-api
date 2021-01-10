#!/bin/bash

export DB_DRIVER='com.mysql.cj.jdbc.Driver'
export DB_URL='jdbc:mysql://localhost:3306/crmdb?createDatabaseIfNotExist=true'
export DB_USERNAME='root'
export DB_PWD='changeme'
export DB_VALIDATION='SELECT 1'

java -jar target/crm-service-api-1.0.jar db migrate /config.yml
java -jar target/crm-service-api-1.0.jar create-admin-user -p changeme /config.yml
java -jar target/crm-service-api-1.0.jar server /config.yml


