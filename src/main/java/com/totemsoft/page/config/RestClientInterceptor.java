package com.totemsoft.page.config;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import com.totemsoft.page.exchangerates.v1.api.ExchangeRatesApi;
import com.totemsoft.page.marketstack.v2.api.MarketStackApi;

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
        log.debug("URI: {}", uri.getPath());
        final var path = uri.getPath();
        final var query = uri.getQuery();
        final var keyValues = query.split("&");
        final var params = keyValues.length == 0 ? Map.<String, String>of() : Arrays.stream(keyValues)
            .filter(elem -> !elem.startsWith(MarketStackApi.ACCESS_KEY))
            .map(elem -> elem.split("="))
            .collect(Collectors.toMap(e -> ArrayUtils.get(e, 0), e -> ArrayUtils.get(e, 1, "")));
        log.debug("URI params: {}", params);
        final var limit = Optional.ofNullable(params.get(MarketStackApi.LIMIT));
        final var offset = Optional.ofNullable(params.get(MarketStackApi.OFFSET));
        final var exchange = Optional.ofNullable(params.get(MarketStackApi.EXCHANGE));
        final var search = Optional.ofNullable(params.get(MarketStackApi.SEARCH));
        final var symbols = Optional.ofNullable(params.get(MarketStackApi.SYMBOLS));
        final var base = Optional.ofNullable(params.get(ExchangeRatesApi.BASE));
        // replace any character that is NOT a letter, number, dot, underscore, or hyphen with an underscore
        final var fileName = path.replaceAll("[^a-zA-Z0-9._-]", "_")
            + '_' + limit.orElse("")
            + '_' + offset.orElse("")
            + '_' + exchange.orElse("")
            + '_' + search.orElse("")
            + '_' + symbols.orElse("")
            + '_' + base.orElse("")
            + ".json";
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
