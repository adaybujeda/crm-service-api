# Agile Monkeys - Crm Service API - crm-service-api
RESTfull API to manage users and customers. Basic CRUD operations to manage User and Customer entities.

### Local environment
The service has been developed using **OpenJDK 11** and **Maven 3.6.3**

### Running the service locally
**With local DB under `/tmp/crm-service-db`**
* Build the service:
* `mvn clean install`
* Delete the database to start fresh:
* `rm /tmp/crm-service-db.*`
* Create DB tables:
* `DB_URL=jdbc:h2:/tmp/crm-service-db java -jar target/crm-service-api-1.0.jar db migrate /config.yml`
* Create default ADMIN user `username=admin`. Provide desired password
* `DB_URL=jdbc:h2:/tmp/crm-service-db java -jar target/crm-service-api-1.0.jar create-admin-user -p changeme /config.yml`
* Start the service:
* `DB_URL=jdbc:h2:/tmp/crm-service-db java -jar target/crm-service-api-1.0.jar server /config.yml`
* Some test URLs:
* `curl -v http://localhost:8081/healthcheck`
* `curl -v http://localhost:8080/crm/users`
* [Postman collection](docs/crm-service-api.postman.json)

## API specification
**Auth**  
[Auth API specification](docs/api-spec/crm-service-auth-api.raml)

**User management**  
[Users API specification](docs/api-spec/crm-service-users-api.raml)

**Customers**  
[Customers API specification](docs/api-spec/crm-service-customers-api.raml)  
[Ciustomer's photo API specification](docs/api-spec/crm-service-customers-photo-api.raml)

To secure the requests, the standard `Authorization: Bearer` mechanism is used. The token will be requested by the client via the login WS.

To prevent concurrent modifications an entity version number will be used. Get entity responses will include the `ETag` header, it will contain the entity version number.
Update requests will send an `If-Match` header with the version number to ensure no other update has been completed since last read.
The service will return a `412` response if the version numbers do not match.

**CORS**
This service was designed as a back-end to back-end api. If there are use cases where a front-end app (from a browser) will be using the API, we need to implement a filter to return the appropriate CORS headers.

## API Storage
As the services and data entities are very simple, we could have selected almost any type of storage.  
To keep it simple, I am going to use an SQL solution. For local development, I will be using an in memory DB: **H2**

Between the different off-the-shelve options that Dropwizard provides to interact with SQL databases, I have selected **JDBI3**.
It provides a simple SQL interface, easy to implement and understand by developers. As well, because I have some previous experience with it.

I have taken a look at Hibernate, it looks like a better approach, but need I need more time to understand it better before I can use it.
For things like this, we usually create a new story, important but not critical, and move it to the next phase of the project.

Other considerations when selecting storage:
* Has the company invested in a certain storage technology/cluster.
* What is the expertise in the team/company for certain types, engines and libraries.
* Is there a need to explore new technologies? If this is a small, low-key project, we could use it to learn new things.

### Photo storage
To store the photos in the system, I have considered several options:
* **Database storage**
* **Filesystem storage**
* **Third-party service (eg: Amazon S3)**

My preferred option for storing images would have been to use a cloud service. This provides highly available, highly resilient storage, with the possibility of serving these files directly from the third party infrastructure.  
But using these services is not always possible or effective, it is expensive in the long term, external dependency to manage, the photos are stored off premise, we need to establish a commercial relationship with the third party and do due diligence.
Sometimes, for small projects/teams, this is not efficient.

In our case, to keep the project simple, I have chosen the database storage to reuse the database we are already using. I have chosen the database over file storage to make the system a bit more portable.  

I will be following some recommendations I found while doing the analysis: store the photos in their own table and using a `VARBINARY` field. As well, limiting the photo size to around `1MB`.

### Database change management - Dropwizard Migrations - Liquibase
This might have been a bit too much for this project, but I wanted to show that automating DB changes are an important part of the dev lifecycle.

The idea is to execute the migrations on every deployment to keep the DB and code in sync.
The SQL files with the database tables can be found here: [Database table definitions](src/main/resources/migrations)

## Credentials Management
As per requirements, the system should manage Users and provide an authentication mechanism.
The authentication mechanism that I have chosen is to validate a username and password through a login WS.
The service will return a JWT token that will be used to authorize requests to the system.

I am not a security expert, but there are some basics that the system should implement to make the credentials secure:
* Do not store plain text passwords
* Store password using a non-reversible cryptographic function
* Do not return the password field in WS responses.

In order to follow these basic requirements, I will be implementing the following:
* Store the password hash in the database and discard the original.
* A login WS to authenticate users comparing the hashes.
* The update user WS will not support the password field.
* A reset password WS to set a new password.

### Password hash
For the hash function, after ready some articles on password security, I am going to use BCrypt.  
Spring security offers a great utilities for hashing:
`spring-security-crypto` - `org.springframework.security.crypto.bcrypt.BCrypt`

### JWT libraries
After taking a look at the libraries recommended by the JWT site: `https://jwt.io/`, `fusionauth-jwt` offered the implementation that I needed for the tokens, it is easy to use and provided by a well established company in the auth space.  
`https://github.com/FusionAuth/fusionauth-jwt`

## Technical Notes
**Resource Collections - Pagination:**
I have left out collections pagination initially. This is for getting all users or customers WS.
This will be a problem once the users or customer collection reaches a certain number of items.

Could be added at a later point, but clients will have to be updated to get the full list of items.

**JWT tokens:**
These are generated containing the user role. Tokens could have the wrong role or be valid for a deleted user until it expires.
An additional validation could be done when decoding the token, but decided not to implement it for simplicity and performance.

**Usernames:**
As there are no explicit requirements, usernames are case-insensitive.

**Delete User:**
To be client friendly, deletes are always successful regardless of whether a user was deleted or not.  
A "feature" I discovered while testing is that a user can delete its own user record.

**ForbiddenException:**
When a request is authenticated, but the role is not authorized for a particular resource, Dropwizard returns a 403 with tha custom error message.
This message is not consistent with the other error messages. We should create a new ExceptionMapper to fix this.

**SSL:**
Should we use SSL for the final hop to access this service?
I am assuming that SSL will be provided at the outer layers of the system, but will it be needed inside the data center?

**[Phase 2 considerations](docs/phase2.md)**