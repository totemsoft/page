FROM eclipse-temurin:25-jre-alpine

LABEL Maintainer="Valeri Chibaev <shibaev.valera@gmail.com>"

# run as root
#USER root
# fix vulnerabilities
RUN echo $(cat /etc/os-release)
#RUN yum update -y && yum clean all

# timezone
ARG tz
#ENV TZ=${tz:-UTC}
ENV TZ=${tz:-Australia/Brisbane}
RUN echo $TZ
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
RUN echo "date="$(date)

# -S: Create a system group/user
RUN addgroup -S totemsoft && adduser -S admin -G totemsoft -s/sbin/nologin
USER admin:totemsoft

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
