FROM eclipse-temurin:21-jdk-alpine

WORKDIR /clab

COPY build/libs/*SNAPSHOT.jar clab.jar

ENTRYPOINT ["java", "-jar", "clab.jar"]