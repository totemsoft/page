package com.totemsoft.page;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
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
        cleanupDirectory();
    }

    private static void cleanupDirectory() {
        try {
            final var path = System.getenv("EFS_MOUNT_PATH");
            if (path != null) {
                final var dbName = System.getenv("DB_NAME");
                final var sb = new StringBuilder();
                sb.append("Cleanup " + path);
                cleanupDirectory(path, Optional.ofNullable(dbName))
                    .forEach(d -> sb.append("\n\t" + d));
                log.debug(sb.toString());
            }
        } catch (Throwable e) {
            log.error("FAILED to cleanupDirectory:", e);
        }
    }

    private static List<String> cleanupDirectory(String path, Optional<String> dbName) throws IOException {
        try (Stream<Path> walk = Files.walk(Paths.get(path))) {
            return walk
                .filter(Files::isRegularFile)
                .map(p -> {
                    final var file = p.toFile();
                    final var fileName = file.getName();
                    final var lastModified = Instant.ofEpochMilli(file.lastModified())
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();
                    try {
                        final var owner = Files.getOwner(p, LinkOption.NOFOLLOW_LINKS).getName();
                        boolean deleted = false;
                        if (dbName.isPresent() && !fileName.startsWith(dbName.get() + ".")) {
                            deleted = Files.deleteIfExists(p);
                        }
                        return lastModified + "\t" + owner + "\t" + p.toString() + (deleted ? " [*]" : "");
                    } catch (IOException ignore) {
                        return "ERROR:\t" + p.toString() + ":\t" + ignore.getMessage();
                    }
                })
                .collect(Collectors.toUnmodifiableList());
        }
    }

}
