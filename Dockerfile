FROM maven:3.8.3-openjdk-17 AS build
WORKDIR /workspace/app
COPY . /workspace/app/.
RUN mvn -f /workspace/app/pom.xml clean package -pl :qoute-service -am -DskipTests

FROM openjdk:17-jdk-slim
COPY --from=build /workspache/app/qoute-service/target/qoute-service-v.1.jar /quote-service.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/quote-service.jar"]