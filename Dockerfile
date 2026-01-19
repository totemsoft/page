FROM eclipse-temurin:17-jre-alpine

LABEL Maintainer="Valeri Chibaev <shibaev.valera@gmail.com>"

# run as root
#USER root
# fix vulnerabilities
RUN echo $(cat /etc/os-release)
#RUN yum update -y && yum clean all
#RUN $(yes | rm /usr/bin/python)

# timezone
ARG tz
#ENV TZ=${tz:-UTC}
ENV TZ=${tz:-Australia/Brisbane}
RUN echo $TZ
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
RUN echo "date="$(date)

RUN addgroup -S totemsoft && adduser -S admin -G totemsoft
USER admin:totemsoft

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
