FROM container-registry.oracle.com/graalvm/native-image:25

LABEL Maintainer="Valeri Chibaev <shibaev.valera@gmail.com>"

#USER root
RUN echo $(cat /etc/os-release)
# fix vulnerabilities
#RUN yum update -y && yum clean all

#RUN yum install amazon-efs-utils
#RUN systemctl enable --now amazon-ecs-volume-plugin

# timezone
ARG tz
ENV TZ=${tz:-UTC}
RUN echo $TZ
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
RUN echo "date="$(date)

RUN groupadd -g 1001 totemsoft && useradd admin -g totemsoft -u 1001 -s/sbin/nologin
USER admin:totemsoft

#WORKDIR /app
ARG EXECUTABLE_FILE=./target/page-builder
COPY ${EXECUTABLE_FILE} page-builder

ENTRYPOINT ["/bin/bash", "-c", "./page-builder"]
