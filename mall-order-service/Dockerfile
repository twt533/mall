FROM openjdk:11-jre-slim

WORKDIR /app
COPY mall-order-service/target/*.jar app.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "app.jar"]
