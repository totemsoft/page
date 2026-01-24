package com.totemsoft.page;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
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
            final var sb = new StringBuilder();
            sb.append("Listing " + path);
            listDirectory(path).forEach(d -> sb.append("\n\t" + d));
            log.debug(sb.toString());
        } catch (IOException e) {
            log.error("FAILED to listDirectory:", e);
        }
    }

    private static List<String> listDirectory(String path) throws IOException {
        try (Stream<Path> walk = Files.walk(Paths.get(path))) {
            return walk
                //.filter(Files::isRegularFile)
                .map(p -> {
                    String owner = null;
                    try {
                        owner = Files.getOwner(p, LinkOption.NOFOLLOW_LINKS).getName();
                    } catch (IOException e) {
                        owner = e.getMessage();
                    }
                    final var d = Instant.ofEpochMilli(p.toFile().lastModified())
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();
                    return d + "\t" + owner + "\t" + p.toString();
                })
                .collect(Collectors.toUnmodifiableList());
        }
    }

}
