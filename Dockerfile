#FROM maven:3.6.3-openjdk-11 AS builder
FROM ghcr.io/graalvm/graalvm-ce:java11-21.0.0.2 AS builder

RUN gu install native-image

ENV M2_SETTINGS_PATH "/root/.m2/settings.xml"
ARG github_token
ENV GITHUB_TOKEN ${github_token}

COPY write_repo_settings.sh /tmp/write_repo_settings.sh
RUN mkdir -p /root/.m2
RUN /tmp/write_repo_settings.sh
COPY ./src /usr/src/app/src
COPY ./.mvn /usr/src/app/.mvn
COPY ./mvnw* /usr/src/app
COPY ./pom.xml /usr/src/app

WORKDIR /usr/src/app

RUN ./mvnw package -Dpackaging=native-image


FROM frolvlad/alpine-glibc:alpine-3.12
RUN apk update && apk add libstdc++

EXPOSE 7000
ENV APP_NAME=recognizers-service

COPY --from=builder /usr/src/app/target/${APP_NAME} /app/application

ENTRYPOINT ["/app/application"]
