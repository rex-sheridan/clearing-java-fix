# Build stage
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
# Download dependencies first to leverage Docker cache
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/target/clearing-fix-demo-0.0.1-SNAPSHOT.jar app.jar

# Expose FIX acceptor port
EXPOSE 9876
# Expose HTTP actuator port
EXPOSE 8080

# Default to running the ClearingHouseApp (Acceptor)
# This can be overridden at runtime to run MemberFirmApp (Initiator)
# by changing the entrypoint or passing the class name if needed.
# However, both apps are packaged in the same jar.
ENTRYPOINT ["java", "-jar", "--add-opens", "java.base/java.lang=ALL-UNNAMED", "/app/app.jar"]
