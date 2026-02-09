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

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

import lombok.extern.log4j.Log4j2;

@SpringBootApplication
@Log4j2
public class Application {

    public static void main(String[] args) {
        copyDatabase(System.getenv("DB_NAME_PREV"));
        final var ctx = SpringApplication.run(Application.class, args);
        cleanupDirectory(ctx.getEnvironment());
    }

    private static void copyDatabase(String dbNamePrev) {
        if (StringUtils.isBlank(dbNamePrev)) {
            return;
        }
        final var dbPath = System.getenv("DB_PATH");
        final var dbName = System.getenv("DB_NAME");
        final var dbSuffix = ".mv.db";
        if (dbPath == null || dbNamePrev == null || dbName == null) {
            log.info("Could not copy from DB_PATH={}: DB_NAME_PREV={} -> DB_NAME={}", dbPath, dbNamePrev, dbName);
            return;
        }
        try {
            final var source = Paths.get(dbPath + '/' + dbNamePrev + dbSuffix);
            final var target = Paths.get(dbPath + '/' + dbName + dbSuffix);
            final var result = Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            log.info("File copied: {} -> {} = {}", source, target, result);
        } catch (Exception e) {
            log.error("FAILED to copyDatabase: [{}] {}", e.getClass(), e.getMessage());
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
                        if (dbName.isPresent() && !fileName.startsWith(dbName.get() + ".")
                                && !Files.deleteIfExists(p)) {
                            log.warn("FAILED to delete: {}", p); // owner = root ?
                        }
                        return lastModified + "\t" + owner + "\t" + p.toString() + (!file.exists() ? " [*]" : "");
                    } catch (Exception ignore) {
                        return "WARN:\t" + p.toString() + ":\t" + ignore.getMessage();
                    }
                })
                .toList();
        }
    }

}
