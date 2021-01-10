#!/bin/bash

java -jar target/crm-service-api-1.0.jar db migrate /config.yml
java -jar target/crm-service-api-1.0.jar create-admin-user -p $ADMIN_PASSWORD /config.yml
java -jar target/crm-service-api-1.0.jar server /config.yml


