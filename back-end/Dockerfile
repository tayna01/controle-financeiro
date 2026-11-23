FROM eclipse-temurin:21-jdk-alpine
VOLUME /tmp
COPY target/*.jar backend.jar
ENTRYPOINT ["java", "-jar", "/backend.jar"]