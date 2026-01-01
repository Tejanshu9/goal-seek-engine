# Use Java 17 JDK image
FROM eclipse-temurin:17-jdk

# Set working directory inside container
WORKDIR /app

# Copy Maven wrapper files (optional but good practice)
COPY pom.xml .

# Copy source code
COPY src ./src

# Build the application
RUN ./mvnw clean package -DskipTests || mvn clean package -DskipTests

# Expose application port
EXPOSE 8080

# Run the Spring Boot application
CMD ["java", "-jar", "target/goal-seek-engine-1.0.0.jar"]
