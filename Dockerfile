# ============================
# ETAPA 1: BUILD CON MAVEN
# ============================
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app

# Copiar pom.xml y descargar dependencias (cache)
COPY pom.xml .
RUN mvn -q dependency:go-offline

# Copiar el código fuente y compilar
COPY src ./src
RUN mvn clean package -DskipTests


# ============================
# ETAPA 2: IMAGEN FINAL
# ============================
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copiar el jar empaquetado desde la etapa builder
COPY --from=builder /app/target/*.jar app.jar

# Puerto de tu app Spring Boot
EXPOSE 8080

ENV JAVA_OPTS=""

# Comando de arranque
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
