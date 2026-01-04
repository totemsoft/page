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

# References
[Spring Boot: Managed Dependency Coordinates](https://docs.spring.io/spring-boot/appendix/dependency-versions/coordinates.html)
[Securing a Web Application](https://spring.io/guides/gs/securing-web)
[OAuth 2.0 Login](https://docs.spring.io/spring-security/reference/reactive/oauth2/login/index.html)
[MapStruct: Reference Guide](https://mapstruct.org/documentation/stable/reference/html/)
[Tutorial: Using Thymeleaf](https://www.thymeleaf.org/doc/tutorials/3.1/usingthymeleaf.html)
[Thymeleaf + Spring Security integration basics](https://www.thymeleaf.org/doc/articles/springsecurity.html)
