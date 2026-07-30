FROM openjdk:11-jre-slim

WORKDIR /app
COPY mall-gateway/target/*.jar app.jar

EXPOSE 8000

ENTRYPOINT ["java", "-jar", "app.jar"]
