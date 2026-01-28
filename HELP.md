# page-builder
    sdk list java
    sdk install java 25.0.1-tem

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
    docker build . --file docker/java/Dockerfile -t page-builder
    docker run -p 8080:8080 page-builder

## Option 2: Using Cloud Native Buildpacks (Dockerfile ignored)
    ./mvnw spring-boot:build-image -Dspring-boot.build-image.imageName=page-builder-native
    docker run -p 8080:8080 docker.io/library/page-builder-native:latest

## Option 3: Using GraalVM Native Image technology
    curl -s "https://get.sdkman.io" | bash
    sdk install java 25-graal
    ./mvnw -Pnative package
    ./target/page-builder

## Charts
* [highcharts](https://github.com/highcharts/highcharts/tree/v3.0.0)

## References
* [Spring Boot: Common Application Properties](https://docs.spring.io/spring-boot/appendix/application-properties/index.html)
* [Spring Boot: Managed Dependency Coordinates](https://docs.spring.io/spring-boot/appendix/dependency-versions/coordinates.html)
* [Spring Boot: Database Initialization](https://docs.spring.io/spring-boot/how-to/data-initialization.html)
* [Securing a Web Application](https://spring.io/guides/gs/securing-web)
* [OAuth 2.0 Login](https://docs.spring.io/spring-security/reference/reactive/oauth2/login/index.html)
* [MapStruct: Reference Guide](https://mapstruct.org/documentation/stable/reference/html/)
* [Tutorial: Using Thymeleaf](https://www.thymeleaf.org/doc/tutorials/3.1/usingthymeleaf.html)
* [Thymeleaf + Spring Security integration basics](https://www.thymeleaf.org/doc/articles/springsecurity.html)
* [How to use Thymeleaf for JavaScript in Spring Boot](https://stackoverflow.com/questions/77024439/how-to-use-thymeleaf-for-javascript-in-spring-boot)
* [Native Images with Spring Boot and GraalVM](https://www.baeldung.com/spring-native-intro)
* [Building Native Images with Maven: An End-to-End Guide](https://graalvm.github.io/native-build-tools/latest/end-to-end-maven-guide.html)
* [Spring Framework: Ahead of Time Optimizations](https://docs.spring.io/spring-framework/reference/core/aot.html#aot.hints.register-reflection)
* [Download Oracle GraalVM](https://www.graalvm.org/downloads/)
