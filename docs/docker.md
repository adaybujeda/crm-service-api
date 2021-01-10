## Build and run the service using Docker
I have created a [Dockerfile](../docker/Dockerfile) as a CI server and to run the service locally.

The service supports some configuration values to be overridden with environment variables. for all available variables take a look at the [config.yml file](../src/main/resources/config.yml)

Run these commands from the project root folder.

Building the `crm-service` Docker image:  
`docker build -t crm-service:latest -f docker/Dockerfile .`

Run the image:  
`docker run --name crm-service --env-file docker/h2.env -d -p 8080:8080 crm-service`

Wait until the service starts up.  
`docker logs -f crm-service`

The service should be available in port `8080` eg: [http://localhost:8080/auth/login](http://localhost:8080/auth/login)

[Postman collection](crm-service-api.postman.json) to test the resources locally

## Running with MySQL
I have tested the application against a MySQL server using Docker. The JDBC drivers are already configured in the Maven POM.

### Running MySQL and crm-service in Docker
* Build the `crm-service`, follow the steps above
* `docker network create crm-network`
* `docker run --name crm-mysql --network crm-network --network-alias mysql -e MYSQL_ROOT_PASSWORD=changeme -d mysql:8`
* Wait for the server to start up: `docker logs -f crm-mysql`
* `docker run --name crm-service --network crm-network --env-file docker/mysql.env -d -p 8080:8080 crm-service`

### Testing locally using MySQL server
Start MySQL with `root` password `changeme` on port `3306`:  
`docker run --name crm-mysql -e MYSQL_ROOT_PASSWORD=changeme -d -p 3306:3306 mysql:8`

Wait until the server starts up.  
`docker logs -f crm-mysql`

Set the following environment variables in your IDE or shell to connect to MySQL:
* DB_DRIVER='com.mysql.cj.jdbc.Driver'
* DB_URL='jdbc:mysql://localhost:3306/crmdb?createDatabaseIfNotExist=true'
* DB_VALIDATION='SELECT 1'
* DB_USERNAME='root'
* DB_PWD='changeme'

EG: Execute the following script from the root of the project to run service locally against MySQL. Environment variables already et by script:
* create the service jar first: `mvn clean install`
* Run the service against MySQL
* `docker/run-crm-service-mysql.sh`
* Run tests against MySQL
* `docker/run-mvn-mysql.sh`