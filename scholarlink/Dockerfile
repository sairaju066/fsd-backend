# Stage 1: Build
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run with embedded Tomcat (JAR)
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/scholarlink-backend-1.0.0.war app.war
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.war"]