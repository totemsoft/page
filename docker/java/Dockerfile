FROM eclipse-temurin:25-jre-alpine

LABEL Maintainer="Valeri Chibaev <shibaev.valera@gmail.com>"

# run as root
#USER root
# fix vulnerabilities
RUN echo $(cat /etc/os-release)
#RUN apk update && apk clean all

RUN apk add efs-utils
#RUN systemctl enable --now amazon-ecs-volume-plugin

# timezone
ARG tz
ENV TZ=${tz:-UTC}
RUN echo $TZ
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
RUN echo "date="$(date)

# -S: Create a system group/user
RUN addgroup -S totemsoft && adduser -S admin -G totemsoft -s/sbin/nologin
#USER admin:totemsoft

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} page-builder.jar
ENTRYPOINT ["java", "-jar", "/page-builder.jar"]
