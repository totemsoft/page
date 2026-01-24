FROM container-registry.oracle.com/graalvm/native-image:25

LABEL Maintainer="Valeri Chibaev <shibaev.valera@gmail.com>"

# run as root
#USER root
# fix vulnerabilities
RUN echo $(cat /etc/os-release)
#RUN yum update -y && yum clean all

#RUN yum install amazon-efs-utils
#RUN systemctl enable --now amazon-ecs-volume-plugin

# timezone
ARG tz
ENV TZ=${tz:-UTC}
RUN echo $TZ
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
RUN echo "date="$(date)

ARG efsMountPath
ENV EFS_MOUNT_PATH=${efsMountPath:-/mnt/efs}
RUN echo $EFS_MOUNT_PATH
RUN mkdir -p $EFS_MOUNT_PATH

RUN groupadd totemsoft && useradd admin -g totemsoft -s/sbin/nologin

RUN chgrp -R totemsoft $EFS_MOUNT_PATH
RUN chmod -R g+rw $EFS_MOUNT_PATH
#RUN chown -R admin:totemsoft $EFS_MOUNT_PATH

# java.nio.file.AccessDeniedException: /mnt/efs/db/pagedb.mv.db
#USER admin:totemsoft

#WORKDIR /app
ARG EXECUTABLE_FILE=./target/page-builder
COPY ${EXECUTABLE_FILE} page-builder

ENTRYPOINT ["/bin/bash", "-c", "./page-builder"]
