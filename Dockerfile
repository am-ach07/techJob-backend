# 1️⃣ مرحلة البناء (Build) باستخدام صورة Maven
FROM maven:3.9.4-openjdk-21 AS builder
WORKDIR /app

COPY pom.xml .
COPY src ./src

# هذا يبني JAR
RUN mvn clean package -DskipTests

# 2️⃣ مرحلة التشغيل
FROM eclipse-temurin:21-jdk

WORKDIR /app

# نسخه من مرحلة builder
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 7777

ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=dev"]