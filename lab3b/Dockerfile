FROM openjdk:latest
LABEL authors="awbd"
FROM openjdk:latest
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]

