FROM openjdk:21-jdk-slim

COPY /target/checker-1.0.jar app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]