FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY target/serviceRegistry-0.0.1-SNAPSHOT.jar /app
EXPOSE 8761
CMD ["java","-jar", "serviceRegistry-0.0.1-SNAPSHOT.jar"]