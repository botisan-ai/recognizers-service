FROM maven:3.6.3-openjdk-11 AS build
COPY repo-settings.xml /root/.m2/settings.xml
COPY src /usr/src/app/src
COPY pom.xml /usr/src/app
RUN mvn -f /usr/src/app/pom.xml clean package

FROM openjdk:11

ENV APP_NAME=recognizers-service
ENV APP_VERSION=0.1
EXPOSE 6000

COPY --from=build /usr/src/app/target/$APP_NAME-$APP_VERSION.jar /opt/app/$APP_NAME-$APP_VERSION.jar

ENTRYPOINT java -jar /opt/app/$APP_NAME-$APP_VERSION.jar
