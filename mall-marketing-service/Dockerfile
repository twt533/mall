FROM openjdk:11-jre-slim

WORKDIR /app
COPY mall-marketing-service/target/*.jar app.jar

EXPOSE 8086

ENTRYPOINT ["java", "-jar", "app.jar"]
