FROM openjdk:11-jre-slim

WORKDIR /app
COPY mall-product-service/target/*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]
