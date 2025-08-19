# Etapa 1: Build con Gradle
FROM gradle:8.10.2-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle clean bootJar --no-daemon

# Etapa 2: Imagen final minimalista con Amazon Corretto
FROM amazoncorretto:21
WORKDIR /app
COPY --from=build /app/build/libs/SaludoNombre.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
