
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


# FROM openjdk:17-jdk-slim AS build

# COPY pom.xml mvnw ./
# COPY .mvn .mvn
# RUN ./mvnw dependency:resolve

# COPY src src
# RUN ./mvnw package

# FROM openjdk:17-jdk-slim
# WORKDIR contact-api
# COPY --from=build target/*.jar contact-api.0.0.1-SNAPSHOT.jar
# ENTRYPOINT ["java", "-jar", "contact-api.0.0.1-SNAPSHOT.jar"]
FROM ubuntu:latest AS build
RUN apt-get-update
RUN apt-get install openjdk-17-jdk -y
COPY . .


FROM  maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17.0.1-jdk-slim
COPY --from=build /target/contact-api.0.0.1-SNAPSHOT.jar contact-api.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar","contact-api.jar"]


#FROM eclipse-temurin:17-jdk-alpine
#VOLUME /tmp
#ARG JAR_FILE
#COPY ${JAR_FILE} app.jar
#ENTRYPOINT ["java","-jar","/app.jar"]
