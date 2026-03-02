FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
# Cria as pastas que o serviço usa
RUN mkdir -p /app/data /app/output
ENTRYPOINT ["java", "-jar", "app.jar"]