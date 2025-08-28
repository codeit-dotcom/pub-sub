# Use official JDK image
FROM eclipse-temurin:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy project files
COPY . .

# Build project (skip tests for speed)
RUN ./mvnw clean package -DskipTests

# Run the JAR
CMD ["java", "-jar", "target/*.jar"]
