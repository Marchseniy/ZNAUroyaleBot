FROM openjdk:17-jdk-slim AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src
RUN ./mvnw clean package -DskipTests

FROM openjdk:17-jre-slim

WORKDIR /app

COPY --from=build /app/target/ZNAUroyaleBot-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]