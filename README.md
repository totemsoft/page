# page
mvn -version
mvn clean install -Dtest -DfailIfNoTests=false

mvn eclipse:clean eclipse:eclipse
mvn dependency:sources
mvn dependency:tree -DoutputFile=dependency.txt

./mvnw spring-boot:run
mvn spring-boot:run -Dspring-boot.run.profiles=local

ps aux | grep java
kill -9 PID

#Docker
## Option 1: Using a Multi-Stage Dockerfile
./mvnw clean install
docker build -t page-builder .
docker run -p 8080:8080 page-builder

## Option 2: Using Cloud Native Buildpacks
./mvnw spring-boot:build-image
docker run -p 8080:8080 docker.io/library/page-builder:0.0.1-SNAPSHOT

# References
[Spring Boot: Managed Dependency Coordinates](https://docs.spring.io/spring-boot/appendix/dependency-versions/coordinates.html)
[Securing a Web Application](https://spring.io/guides/gs/securing-web)
[OAuth 2.0 Login](https://docs.spring.io/spring-security/reference/reactive/oauth2/login/index.html)
[MapStruct: Reference Guide](https://mapstruct.org/documentation/stable/reference/html/)
[Tutorial: Using Thymeleaf](https://www.thymeleaf.org/doc/tutorials/3.1/usingthymeleaf.html)
[Thymeleaf + Spring Security integration basics](https://www.thymeleaf.org/doc/articles/springsecurity.html)
[How to use Thymeleaf for JavaScript in Spring Boot](https://stackoverflow.com/questions/77024439/how-to-use-thymeleaf-for-javascript-in-spring-boot)
