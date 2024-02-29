FROM maven:3.8.5-openjdk-17 as maven-builder
COPY src /etd/src

COPY pom.xml /etd

# COPY frontend /etd/frontend

RUN mvn -f /etd/pom.xml clean install -Pproduction -DskipTests

FROM eclipse-temurin:17-jdk-focal 

# FROM openjdk:17-alpine

COPY --from=maven-builder etd/target/contact-api-0.0.1-SNAPSHOT.jar contact-api-0.0.1-SNAPSHOT.jar

WORKDIR /etd

EXPOSE 8081

ENTRYPOINT ["java","-jar","/contact-api-0.0.1-SNAPSHOT.jar"]
