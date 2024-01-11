FROM maven:3.8.5-openjdk-17-slim as build-image
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package

FROM eclipse-temurin:17.0.9_9-jre-alpine
WORKDIR /app
COPY --from=build-image /app/target/*.jar app.jar
CMD ["java","-jar","app.jar"]