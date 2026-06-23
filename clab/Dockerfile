FROM eclipse-temurin:21-jdk-alpine

WORKDIR /clab

COPY build/libs/*SNAPSHOT.jar clab.jar

# 4. 컨테이너가 켜질 때 실행할 명령어를 지정합니다. (java -jar app.jar)
ENTRYPOINT ["java", "-jar", "clab.jar"]