FROM gradle:8-jdk21-alpine AS build
WORKDIR /app

COPY openapi.yaml ../openapi.yaml
COPY activity-management-backend/build.gradle.kts ./build.gradle.kts
COPY activity-management-backend/settings.gradle.kts ./settings.gradle.kts
COPY activity-management-backend/src ./src

RUN gradle bootJar --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN apk add --no-cache curl

COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]