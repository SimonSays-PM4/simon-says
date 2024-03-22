# SimonSays Backend

## Swagger
### Swagger UI
http://localhost:8080/webjars/swagger-ui/index.html#/

### Api Docs
http://localhost:8080/v3/api-docs

## Project structure
folders: <br>
api: contains everything that the frontend needs. controllers, mappers and types <br>
config: contains all files for configuration.<br>
repository: contains everything for storing the data to the DB.<br>
service: contains all services which are handling the communication between repositories and the api.<br>

## Running the application
To run the application you need to have a running mysql database. You can start the database and the app with the following command:
```shell
docker-compose up -d
```