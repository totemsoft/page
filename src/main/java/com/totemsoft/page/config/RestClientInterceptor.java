package com.totemsoft.page.config;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class RestClientInterceptor implements ClientHttpRequestInterceptor {

    private final Path apiPath;

    public RestClientInterceptor(@Value("${page.apiPath}") String apiPath) throws IOException {
        this.apiPath = Paths.get(apiPath);
        log.debug("apiPath: {}", this.apiPath);
        if (Files.notExists(this.apiPath)) {
            final var path = Files.createDirectories(this.apiPath);
            log.debug("Just created: {}", path);
        }
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        final var response = execution.execute(request, body);
        if (response.getStatusCode().is2xxSuccessful()) {
            save(request.getURI(), response.getBody().readAllBytes());
        }
        return response;
    }

    private void save(URI uri, byte[] body) {
        try {
            final var filePath = getFilePath(uri);
            if (Files.notExists(filePath)) {
                Files.write(filePath, body, StandardOpenOption.CREATE_NEW);
            } else {
                Files.write(filePath, body, StandardOpenOption.TRUNCATE_EXISTING);
            }
        } catch (Exception ignore) {
            log.warn("FAILED to save response:", ignore);
        }
    }

    public Path getFilePath(URI uri) throws IOException {
        log.trace("URI: {}", uri);
        final var path = uri.getPath();
        //final var query = uri.getQuery();
        // replace any character that is NOT a letter, number, dot, underscore, or hyphen with an underscore
        final var fileName = path.replaceAll("[^a-zA-Z0-9._-]", "_") + ".json";
        log.trace(">>> fileName: {}", fileName);
        final var host = uri.getHost();
        final var filePath = apiPath.resolve(host, fileName).normalize();
        log.debug(">>> filePath: {}", filePath);
        if (Files.notExists(filePath.getParent())) {
            Files.createDirectories(filePath.getParent());
        }
        return filePath;
    }

}
