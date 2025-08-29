# First stage: build the JAR
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Second stage: run the JAR
FROM openjdk:17-jdk-slim
VOLUME /tmp
COPY --from=builder /app/target/pubsub-service-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]