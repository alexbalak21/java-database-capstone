# Build stage: Compile the application using Maven
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage: Run the application on a lightweight JDK image
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copy the compiled JAR from the build stage
COPY --from=build /build/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
