FROM eclipse-temurin:21-jdk-alpine
VOLUME /tmp
EXPOSE 8090
COPY target/62RosasTattoo-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]