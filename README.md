# Agile Monkeys - Crm Service API - crm-service-api
RESTfull API to manage users and customers. Basic CRUD operations to manage User and Customer entities.

## API specification
**User management**
[Users API specification](docs/crm-service-users-api.raml)  
To secure the requests, the x-auth-header is used with a token. The token will be requested by the client via a WS still to be defined.

To prevent concurrent modifications, the ETag header is used on GET responses. The ETag header will contain the entity version number.
Update requests will send an If-Match header with the version number to ensure no other update has been completed since last read.
The service will return a 412 response if the version numbers do not match.

## API Storage
As the services and data entities are very simple, we could have selected almost any type of storage.  
To keep it simple, I am going to use an SQL solution. For local development, I will be using an in memory DB: **H2**

Other considerations when selecting storage:
* Has the company invested in a certain storage technology/cluster.
* What is the expertise in the team/company for certain types and engines.
* Is there a need to explore new DB engines? If this is a small, low-key project, we could use it to learn new things.

### Database change management - Dropwizard Migrations - Liquibase
This might have been a bit too much for this project, but I wanted to show that automating DB changes are an important part of the dev lifecycle.

The idea is to execute the migrations on every deployment to keep DB and code in sync.

## Technical Notes
**Resource Collections - Pagination**
I have left out collections pagination initially. This is for getting all users or customers WS.
This will be a problem once the users or customer collection reaches a certain number of items.

Could be added at a later point, but clients will have to be updated to get the full list of items.

**SSL**
Should we use SSL for the final hop to access this service?
I am assuming that SSL will be provided at the outer layers of the system, but will it be needed inside the data center?