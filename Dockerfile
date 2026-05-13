# ---------- Stage 1: Build ----------
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy pom first to use dependency caching
COPY pom.xml .

# Pre-download dependencies for faster builds
RUN mvn -q dependency:go-offline

# Copy all source code
COPY . .

# Make Maven Wrapper executable (if exists)
RUN chmod +x mvnw || true

# Build the application without tests
RUN mvn -q package -DskipTests


# ---------- Stage 2: Runtime ----------
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy the built JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose Spring Boot default port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
