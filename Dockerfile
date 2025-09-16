# Use a lightweight Java 21 image
FROM eclipse-temurin:21-jdk

# Set a working directory inside the container
WORKDIR /app

# Copy all source files into the image
COPY . .

# Build the JAR (skip tests to speed up)
RUN ./mvnw package -DskipTests

# Tell Docker how to start the app
CMD ["java","-jar","target/resumemaker-0.0.1-SNAPSHOT.jar"]
