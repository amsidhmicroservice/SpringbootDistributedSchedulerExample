FROM openjdk:17-alpine
VOLUME /tmp
COPY /target/SpringbootDistributedSchedulerExample-*.jar distributedschedulerservice.jar
COPY src src
COPY Dockerfile Dockerfile
COPY pom.xml pom.xml
ENTRYPOINT ["java", "-jar", "distributedschedulerservice.jar"]