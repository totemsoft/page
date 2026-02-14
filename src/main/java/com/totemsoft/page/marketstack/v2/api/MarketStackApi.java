package com.totemsoft.page.marketstack.v2.api;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.totemsoft.page.marketstack.v2.model.ExchangesResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class MarketStackApi {

    @Value("${page.marketstack.com.access-key}")
    private String accessKey;

    private final RestClient marketStackApiRestClient;

    /**
     * Provides general information about 2817 stock exchanges.
     * @param limit - Pagination limit (results per page). Default 100, maximum 1000.
     * @param offset - Pagination offset (number of results to skip). Default 0.
     * @param search - Search term to filter tickers by name or symbol.
     * @return Exchanges list retrieved.
     */
    public ExchangesResponse exchanges(
            Optional<Integer> limit,
            Optional<Integer> offset,
            Optional<String> search) {
        log.debug(">>> loading exchanges");
        final var response = marketStackApiRestClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/exchanges")
                .queryParam("access_key", accessKey)
                .queryParam("limit", limit.orElse(100))
                .queryParam("offset", offset.orElse(0))
                .queryParam("search", search.orElse(""))
                .build())
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .toEntity(ExchangesResponse.class);
        return response.getBody();
    }

}
