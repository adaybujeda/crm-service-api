## Build and run the service using Docker
I have created a [Dockerfile](../docker/Dockerfile.ci) as a CI server and to run the service locally.

The service supports some configuration values to be overridden with environment variables. for all available variables take a look at the [config.yml file](../src/main/resources/config.yml)

Run these commands from the project root folder.

Building the image with default values:  
`docker build -t crm-service:latest -f docker/Dockerfile.ci .`

Building the image overriding defaults, all arguments in the [Dockerfile](../docker/Dockerfile.ci):
* **PASSWORD:** The admin user password. default credentials username: admin password: changeme
* **SECRET:** JWT secret to sign tokens
* **DB:** JDBC Url to connect to the database
* **DB_DRIVER:** JDBC driver class to connect to the database
* **DB_VAL:** SQL query to validate connection
* **DB_U:** Database username
* **DB_P:** Database password

`docker build --build-arg PASSWORD=santana --build-arg SECRET=test -t crm-service:latest -f docker/Dockerfile.ci .`

Run the image:  
`docker run -d -p 8080:8080 crm-service`

The service should be available in port `8080` eg: [http://localhost:8080/auth/login](http://localhost:8080/auth/login)

[Postman collection](crm-service-api.postman.json) to test the resources locally