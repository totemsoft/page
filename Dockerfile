FROM eclipse-temurin:17-jre-alpine
RUN addgroup -S totemsoft && adduser -S admin -G totemsoft
USER admin:totemsoft
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
