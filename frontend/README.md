# SimonSays Front End

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



