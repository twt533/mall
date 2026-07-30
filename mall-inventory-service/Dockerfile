FROM openjdk:11-jre-slim

WORKDIR /app
COPY mall-inventory-service/target/*.jar app.jar

EXPOSE 8085

ENTRYPOINT ["java", "-jar", "app.jar"]
