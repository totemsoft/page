package com.totemsoft.page;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.log4j.Log4j2;

@SpringBootApplication
@Log4j2
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        try {
            final var path = System.getenv("EFS_MOUNT_PATH");
            log.debug("Listing {}:", path);
            listDirectory(path).forEach(d -> log.debug("\t{}", d));
        } catch (IOException e) {
            log.error("FAILED to listDirectory:", e);
        }
    }

    private static List<String> listDirectory(String path) throws IOException {
        try (Stream<Path> walk = Files.walk(Paths.get(path))) {
            return walk
                //.filter(Files::isRegularFile)
                .map(Path::toString)
                .collect(Collectors.toList());
        }
    }

}
