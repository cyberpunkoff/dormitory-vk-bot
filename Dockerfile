FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /opt/app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline
COPY ./src ./src
RUN ./mvnw clean install
FROM eclipse-temurin:21-jre-alpine AS final
WORKDIR /opt/app
EXPOSE 8080
COPY --from=builder /opt/app/target/dormitory-vk-bot-0.1.jar /opt/app/*.jar
ENTRYPOINT ["java", "-jar", "/opt/app/*.jar"]
