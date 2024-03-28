# this dockerfile is used to build the backend of the application
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# copy all .jar files from the build/libs directory
COPY build/libs/SimonSays-0.0.1-SNAPSHOT.jar /app/app.jar

# Command to run the application
CMD ["java", "-jar", "/app/app.jar"]