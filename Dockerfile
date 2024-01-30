# Compilação
FROM maven:3.8.3-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src

# Variáveis de Ambiente
ENV MAVEN_OPTS="-Dmaven.repo.local=/root/.m2/repository"
ENV MAVEN_CLI_OPTS="-s /usr/share/maven/ref/settings-docker.xml"

RUN mvn package

# Execução
FROM openjdk:17
WORKDIR /app
COPY --from=build /app/target/generic-service-1.0-SNAPSHOT.jar ./generic-service.jar
ENTRYPOINT ["java", "-jar", "generic-service.jar"]