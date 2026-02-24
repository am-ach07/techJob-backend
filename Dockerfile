# 🛠 Build stage ― استخدم صورة Maven تدعم JDK 21
FROM maven:3.8.8-eclipse-temurin-21-alpine AS build
WORKDIR /app

# نسخ ملفات البناء
COPY pom.xml .
COPY src ./src

# بناء الملف JAR
RUN mvn clean package -DskipTests

# 🚀 Run stage ― استخدم Eclipse Temurin JRE 21
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# نسخ JAR من مرحلة البناء
COPY --from=build /app/target/*.jar app.jar

EXPOSE 7777

ENTRYPOINT ["java", "-jar", "app.jar"]