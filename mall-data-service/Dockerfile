FROM openjdk:11-jre-slim

WORKDIR /app
COPY mall-data-service/target/*.jar app.jar

EXPOSE 8087

ENTRYPOINT ["java", "-jar", "app.jar"]
