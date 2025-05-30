FROM eclipse-temurin:17-jdk

WORKDIR /app

ARG JAR_FILE=target/fxapp-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
