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
COPY --from=build /app/target/simt-0.0.1-SNAPSHOT.jar ./simt.jar

ENTRYPOINT ["java", "-jar", "simt.jar"]