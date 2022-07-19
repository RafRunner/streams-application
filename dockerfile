FROM openjdk:17.0-jdk

RUN mkdir /app
COPY ./build/libs/*.jar /app/stream-application.jar
COPY docker.env /app/.env
COPY ./build/resources /app

WORKDIR /app

ENTRYPOINT ["java", "-jar", "stream-application.jar"]