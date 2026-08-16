FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .

RUN mvn --batch-mode dependency:go-offline

COPY src ./src

RUN mvn --batch-mode clean package -DskipTests


FROM eclipse-temurin:21-jre AS runtime

WORKDIR /app

RUN useradd --system --create-home --uid 1001 personarum

COPY --from=build /app/target/*.jar app.jar

RUN chown personarum:personarum app.jar

USER personarum

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
