package com.totemsoft.page;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

import lombok.extern.log4j.Log4j2;

@SpringBootApplication
@Log4j2
public class Application {

    public static void main(String[] args) {
        copyDatabase();
        final var ctx = SpringApplication.run(Application.class, args);
        cleanupDirectory(ctx.getEnvironment());
    }

    private static void copyDatabase() {
        final var dbPath = System.getenv("DB_PATH");
        final var dbNamePrev = System.getenv("DB_NAME_PREV");
        final var dbName = System.getenv("DB_NAME");
        log.info("File to copy from directory {}: {} -> {}", dbPath, dbNamePrev, dbName);
        try {
            final var source = Paths.get(dbPath + '/' + dbNamePrev + ".mv.db");
            final var target = Paths.get(dbPath + '/' + dbName + ".mv.db");
            final var result = Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            log.info("File copied: {} -> {} = {}", source, target, result);
        } catch (Exception e) {
            log.warn("FAILED to copyDatabase: {}", e.getMessage());
        }
    }

    private static void cleanupDirectory(Environment env) {
        try {
            final var dbPath = env.getProperty("page.dbPath");
            final var dbName = env.getProperty("page.dbName");
            final var sb = new StringBuilder();
            sb.append("Cleanup " + dbPath);
            cleanupDirectory(dbPath, Optional.ofNullable(dbName))
                .forEach(d -> sb.append("\n\t" + d));
            log.info(sb.toString());
        } catch (Throwable e) {
            log.warn("FAILED to cleanupDirectory:", e);
        }
    }

    private static List<String> cleanupDirectory(String dbPath, Optional<String> dbName) throws IOException {
        try (Stream<Path> walk = Files.walk(Paths.get(dbPath))) {
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
                        if (dbName.isPresent() && !fileName.startsWith(dbName.get() + ".")) {
                            //Files.deleteIfExists(p);
                        }
                        return lastModified + "\t" + owner + "\t" + p.toString() + (!file.exists() ? " [*]" : "");
                    } catch (Exception ignore) {
                        return "ERROR:\t" + p.toString() + ":\t" + ignore.getMessage();
                    }
                })
                .toList();
        }
    }

}
