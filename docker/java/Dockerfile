FROM eclipse-temurin:25-jre-alpine

LABEL Maintainer="Valeri Chibaev <shibaev.valera@gmail.com>"

# run as root
#USER root
# fix vulnerabilities
RUN echo $(cat /etc/os-release)
#RUN apk update && apk clean all

#RUN apk add efs-utils
#RUN systemctl enable --now amazon-ecs-volume-plugin

# timezone
ARG tz
ENV TZ=${tz:-UTC}
RUN echo $TZ
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
RUN echo "date="$(date)

ARG efsMountPath
ENV EFS_MOUNT_PATH=${efsMountPath:-/mnt/efs/db}
RUN echo $EFS_MOUNT_PATH
RUN mkdir -p $EFS_MOUNT_PATH

ARG dbName
ENV DB_NAME=${dbName:-pagedb}
RUN echo $DB_NAME

RUN addgroup -S totemsoft && adduser -S admin -G totemsoft -s/sbin/nologin

RUN chgrp -R totemsoft $EFS_MOUNT_PATH
RUN chmod -R g+rw $EFS_MOUNT_PATH
#RUN chown -R admin:totemsoft $EFS_MOUNT_PATH

RUN mkdir -p /mnt/efs/db
RUN mkdir -p /mnt/efs/db/db
RUN touch /mnt/efs/db/db/pagedb.mv.db
RUN touch /mnt/efs/db/pagedb_h2.mv.db

# java.nio.file.AccessDeniedException: /mnt/efs/db/pagedb.mv.db
#USER admin:totemsoft

WORKDIR /app
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} page-builder.jar

ENTRYPOINT ["java", "-jar", "page-builder.jar"]
