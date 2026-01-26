FROM eclipse-temurin:25-jre-alpine

LABEL Maintainer="Valeri Chibaev <shibaev.valera@gmail.com>"

#USER root
RUN echo $(cat /etc/os-release)
# fix vulnerabilities
#RUN apk update && apk clean all

#RUN apk add efs-utils
#RUN systemctl enable --now amazon-ecs-volume-plugin

# timezone
ARG tz
ENV TZ=${tz:-UTC}
RUN echo $TZ
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
RUN echo "date="$(date)

ARG userName
ENV USER_NAME=${userName:-admin}
RUN echo "USER_NAME="$USER_NAME

RUN addgroup -S -g 1001 totemsoft && adduser -S $USER_NAME -G totemsoft -u 1001
# -s/sbin/nologin
# Caused by: java.nio.file.AccessDeniedException: /mnt/efs/db/pagedb.mv.db
USER $USER_NAME:totemsoft

WORKDIR /app
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} page-builder.jar

ENTRYPOINT ["java", "-jar", "page-builder.jar"]
