# SimonSays Backend
We use sonar cloud to check the quality of the code.\
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=simonsays-backend&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=simonsays-backend)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=simonsays-backend&metric=coverage)](https://sonarcloud.io/summary/new_code?id=simonsays-backend)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=simonsays-backend&metric=bugs)](https://sonarcloud.io/summary/new_code?id=simonsays-backend)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=simonsays-backend&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=simonsays-backend)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=simonsays-backend&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=simonsays-backend)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=simonsays-backend&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=simonsays-backend)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=simonsays-backend&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=simonsays-backend)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=simonsays-backend&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=simonsays-backend)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=simonsays-backend&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=simonsays-backend)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=simonsays-backend&metric=sqale_index)](https://sonarcloud.io/summary/new_code?id=simonsays-backend)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=simonsays-backend&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=simonsays-backend)

## Swagger
### Swagger UI
http://localhost:8080/swagger-ui/index.html

### API Docs
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
docker compose up -d
```
After that you can start the application with the following command:
```shell
./gradlew bootRun --args='--spring.profiles.active=dev'
```

## Testing
In order to test the application you need to have a running instance of the DB.\
To run the tests you can use the following command:
```shell
./gradlew test
```