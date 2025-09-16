# ---------- Stage 1: Build ----------
FROM maven:3.9.9-eclipse-temurin-21 AS build

# Set the working directory
WORKDIR /app

# Copy everything into the container
COPY . .

# Ensure the wrapper is executable
RUN chmod +x mvnw

# Build the jar without running tests
RUN ./mvnw package -DskipTests

# ---------- Stage 2: Run ----------
FROM eclipse-temurin:21-jre AS run

WORKDIR /app

# Copy the jar from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port 8080 for Render
EXPOSE 8080

# Start the application
ENTRYPOINT ["java", "-jar", "app.jar"]
