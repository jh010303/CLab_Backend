FROM eclipse-temurin:21-jdk-alpine

WORKDIR /clab

COPY target/*.jar clab.jar

ENTRYPOINT ["java", "-jar", "clab.jar"]
