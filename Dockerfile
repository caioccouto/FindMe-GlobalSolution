# Etapa 1: Compilação da aplicação
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Etapa 2: Execução da aplicação
FROM eclipse-temurin:21-jre

WORKDIR /app

# Criar usuário sem privilégios administrativos
RUN useradd --system --uid 10001 appuser

# Copiar o JAR gerado na compilação
COPY --from=build /app/target/*.jar app.jar

# Definir proprietário dos arquivos
RUN chown appuser:appuser app.jar

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
