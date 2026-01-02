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
[Tutorial: Using Thymeleaf](https://www.thymeleaf.org/doc/tutorials/3.1/usingthymeleaf.html)
