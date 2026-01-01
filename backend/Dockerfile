# ===== Build Stage =====
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /build

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

# ===== Runtime Stage (DISTROLESS) =====
FROM gcr.io/distroless/java17-debian12

WORKDIR /app
COPY --from=builder /build/target/*.jar app.jar

EXPOSE 8080
USER nonroot

ENTRYPOINT ["java", "-jar", "app.jar"]
