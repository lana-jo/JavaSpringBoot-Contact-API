
# # Use an official Maven image as the base image
# FROM  maven:3.8.5-openjdk-17 AS build
# # Set the working directory in the container
# WORKDIR /app
# # Copy the pom.xml and the project files to the container
# COPY pom.xml .
# COPY src ./src
# # Build the application using Maven
# RUN mvn clean package -DskipTests
# # Use an official OpenJDK image as the base image
# FROM openjdk:17.0.1-jdk-slim
# # Set the working directory in the container
# WORKDIR /app
#  Copy the built JAR file from the previous stage to the container
# COPY --from=build /app/target/ContactApiapplication.jar .
# # Set the command to run the application
# CMD ["java", "-jar", "ContactApiapplication.jar"]


FROM eclipse-temurin:17-jdk-focal

WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline

COPY src ./src

CMD ["./mvnw", "spring-boot:run"]


#FROM ubuntu:latest AS build
#RUN apt-get-update
#RUN apt-get install openjdk-17-jdk -y
#COPY . .


#FROM  maven:3.8.5-openjdk-17 AS build
#COPY . .
#RUN mvn clean package -DskipTests
#
#FROM openjdk:17.0.1-jdk-slim
#COPY --from=build /target/demo-0.0.1-SNAPSHOT.jar demo.jar
#EXPOSE 8081
#ENTRYPOINT ["java", "-jar","demo.jar"]


#FROM eclipse-temurin:17-jdk-alpine
#VOLUME /tmp
#ARG JAR_FILE
#COPY ${JAR_FILE} app.jar
#ENTRYPOINT ["java","-jar","/app.jar"]
