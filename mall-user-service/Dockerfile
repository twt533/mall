FROM openjdk:11-jre-slim

WORKDIR /app
COPY mall-user-service/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
