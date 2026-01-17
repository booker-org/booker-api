FROM eclipse-temurin:25-jdk AS build

WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./

RUN ./mvnw dependency:go-offline

COPY src/ ./src

RUN ./mvnw clean package

FROM eclipse-temurin:25-jre AS runtime

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

ENV JAVA_OPTS=""

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]