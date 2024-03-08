FROM eclipse-temurin:17

WORKDIR /app

# copy all .jar files from the build/libs directory
COPY build/SimonSays-0.0.1-SNAPSHOT.jar /app/app.jar

# Command to run the application
CMD ["java", "-jar", "/app/app.jar"]