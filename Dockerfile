# Используем официальный образ OpenJDK в качестве базового
FROM openjdk:17-jdk-slim AS build

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем pom.xml и mvnw в рабочую директорию
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Копируем исходный код
COPY src ./src

# Устанавливаем права на выполнение для mvnw
RUN chmod +x mvnw

# Собираем проект
RUN ./mvnw clean package -DskipTests

# Создаем образ для выполнения
FROM openjdk:17-jdk-slim

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем jar файл из сборки
COPY --from=build /app/target/ZNAUroyaleBot-0.0.1-SNAPSHOT.jar ZNAUroyaleBot.jar

# Указываем команду для запуска приложения
ENTRYPOINT ["java", "-jar", "ZNAUroyaleBot.jar"]