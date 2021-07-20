FROM openjdk:15-jdk-alpine
RUN addgroup -S microservice && adduser -S file-api -G microservice
USER file-api:microservice
RUN cd /opt
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]