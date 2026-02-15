package com.totemsoft.page.marketstack.v2.api;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.totemsoft.page.marketstack.v2.model.ExchangeTickerResponse;
import com.totemsoft.page.marketstack.v2.model.ExchangeResponse;

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
    public ExchangeResponse exchanges(
            Optional<Integer> limit,
            Optional<Integer> offset,
            Optional<String> search) {
        log.debug(">>> loading exchanges for: {}, {}, {}", limit, offset, search);
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
            .toEntity(ExchangeResponse.class);
        return response.getBody();
    }

    /**
     * Specific Stock Exchange Tickers
     * @param mic - Exchange MIC identifier (e.g., XNAS).
     * @param limit - Pagination limit (results per page). Default 100, maximum 1000.
     * @param offset - Pagination offset (number of results to skip). Default 0.
     * @return Returns tickers listed on a specific exchange.
     */
    public ExchangeTickerResponse exchangeTickers(
            String mic,
            Optional<Integer> limit,
            Optional<Integer> offset) {
        log.debug(">>> loading exchangeTickers for: {}, {}, {}", mic, limit, offset);
        final var response = marketStackApiRestClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/exchanges/{mic}/tickers")
                .queryParam("access_key", accessKey)
                .queryParam("limit", limit.orElse(100))
                .queryParam("offset", offset.orElse(0))
                .build(Map.of("mic", mic)))
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .toEntity(ExchangeTickerResponse.class);
        return response.getBody();
    }

}
