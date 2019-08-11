FROM gradle:5.5.1-jdk12 as build

WORKDIR /gradle_build

COPY src src
COPY build.gradle .
COPY gradle.properties .
COPY settings.gradle .

RUN gradle --no-daemon clean build


FROM openjdk:14-jdk-alpine3.10

WORKDIR app
VOLUME /tmp
EXPOSE 8080

COPY --from=build /gradle_build/build/libs/spotify-chat.jar .

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app/spotify-chat.jar"]