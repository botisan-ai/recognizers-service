FROM maven:3.6.3-openjdk-11 AS build

ENV M2_SETTINGS_PATH "/root/.m2/settings.xml"
ARG github_token
ENV GITHUB_TOKEN ${github_token}

COPY write_repo_settings.sh /tmp/write_repo_settings.sh
RUN mkdir -p /root/.m2
RUN /tmp/write_repo_settings.sh
COPY src /usr/src/app/src
COPY pom.xml /usr/src/app
RUN mvn -f /usr/src/app/pom.xml clean package

FROM openjdk:11

ENV APP_NAME=recognizers-service
ENV APP_VERSION=0.2
EXPOSE 7000

COPY --from=build /usr/src/app/target/$APP_NAME-$APP_VERSION.jar /opt/app/$APP_NAME-$APP_VERSION.jar

ENTRYPOINT java -jar /opt/app/$APP_NAME-$APP_VERSION.jar
