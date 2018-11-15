FROM openjdk:8-jdk-alpine
VOLUME /tmp
ARG jarfile
COPY ${jarfile} app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
