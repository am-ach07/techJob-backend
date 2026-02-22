# مرحلة البناء (Build)
FROM maven:3.9.4-openjdk-21 AS build
WORKDIR /app

# نسخ pom + ملفات المشروع
COPY pom.xml .
COPY src ./src

# بناء المشروع وتجاهل الاختبارات
RUN mvn clean package -DskipTests

# مرحلة التشغيل (Run)
FROM openjdk:21-jdk
WORKDIR /app

# نسخ الـ JAR من مرحلة البناء
COPY --from=build /app/target/*.jar app.jar

# فتح البورت
EXPOSE 7777

#  ت jjتشغيل التطبيق
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=dev"]