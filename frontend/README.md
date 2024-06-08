# SimonSays Frontend
We use sonar cloud to check the quality of the code.\
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=simonsys-frontend&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=simonsys-frontend)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=simonsys-frontend&metric=coverage)](https://sonarcloud.io/summary/new_code?id=simonsys-frontend)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=simonsys-frontend&metric=bugs)](https://sonarcloud.io/summary/new_code?id=simonsys-frontend)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=simonsys-frontend&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=simonsys-frontend)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=simonsys-frontend&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=simonsys-frontend)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=simonsys-frontend&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=simonsys-frontend)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=simonsys-frontend&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=simonsys-frontend)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=simonsys-frontend&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=simonsys-frontend)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=simonsys-frontend&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=simonsys-frontend)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=simonsys-frontend&metric=sqale_index)](https://sonarcloud.io/summary/new_code?id=simonsys-frontend)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=simonsys-frontend&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=simonsys-frontend)

The FrontEnd of the application is made in React / Typescript and is using vite to build and run.

Additionally, the styling library Tailwind is being used.

## Install
Install the dependencies using the following command:
```sh{ background = true}
npm install
```

## Create API Endpoints
The backend endpoints are stored in the doc.json file which can be used to generate the api calls in the front end using the following command:
```sh{ background = true}
npm run api 
```
## Build Application
Build the application using the following command:
```sh{ background = true}
npm run build
```

## Start Application Locally
```sh{ background = true}
npm start
```

## Run Tests
In order to test the application you need to have a running instance of the DB and the backend.
```sh{ background = true}
npx cypress run --browser chrome --headed
```



