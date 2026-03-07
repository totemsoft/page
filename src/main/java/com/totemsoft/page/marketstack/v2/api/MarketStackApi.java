package com.totemsoft.page.marketstack.v2.api;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.totemsoft.page.marketstack.v2.model.EODResponse;
import com.totemsoft.page.marketstack.v2.model.ExchangeMicEod;
import com.totemsoft.page.marketstack.v2.model.ExchangeResponse;
import com.totemsoft.page.marketstack.v2.model.ExchangeTickerResponse;

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
     * @param mic - Exchange MIC identifier (e.g. XNAS).
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

    /**
     * EOD Data for a Specific Stock Exchange on a Specific Date
     * @param mic - Exchange MIC identifier (e.g. XNAS).
     * @param date - Specific date for EOD data. Format YYYY-MM-DD.
     * @param symbols - One or more comma-separated ticker symbols (e.g., AAPL,MSFT).
     * @param limit - Pagination limit (results per page). Default 100, maximum 1000.
     * @param offset - Pagination offset (number of results to skip). Default 0.
     * @return Returns EOD data for the given date for all symbols on a specific exchange.
     */
    @Deprecated(forRemoval = true, since = "marketStackApi#eodDate used instead")
    public ExchangeMicEod exchangeMicEodDate(
            String mic,
            LocalDate date,
            List<String> symbols,
            Optional<Integer> limit,
            Optional<Integer> offset) {
        log.debug(">>> loading exchangeMicEodDate for: {}, {}, {}, {}, {}", mic, date, symbols, limit, offset);
        final var response = marketStackApiRestClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/exchanges/{mic}/eod/{date}")
                .queryParam("access_key", accessKey)
                .queryParam("symbols", String.join(",", symbols))
                .queryParam("limit", limit.orElse(100))
                .queryParam("offset", offset.orElse(0))
                .build(Map.of("mic", mic, "date", date)))
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .toEntity(ExchangeMicEod.class);
        return response.getBody();
    }

    /**
     * EOD Data for a Specific Date
     * @param exchange - Filter your results based on a specific stock exchange by specifying the MIC identification of a stock exchange.
     * @param date - Specific date for EOD data. Format YYYY-MM-DD.
     * @param symbols - One or more comma-separated ticker symbols (e.g., AAPL,MSFT).
     * @param limit - Pagination limit (results per page). Default 100, maximum 1000.
     * @param offset - Pagination offset (number of results to skip). Default 0.
     * @param sort - Sort order. Use ASC for oldest first or DESC for newest first.
     * @return
     */
    public EODResponse eodDate(
            LocalDate date,
            Optional<String> exchange,
            List<String> symbols,
            Optional<Integer> limit,
            Optional<Integer> offset,
            Optional<String> sort) {
        log.debug(">>> loading exchangeEodDate for: {}, {}, {}, {}, {}, {}", date, exchange, symbols, limit, offset, sort);
        final var response = marketStackApiRestClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/eod/{date}")
                .queryParam("access_key", accessKey)
                .queryParam("exchange", exchange.orElse(""))
                .queryParam("symbols", String.join(",", symbols))
                .queryParam("limit", limit.orElse(100))
                .queryParam("offset", offset.orElse(0))
                .queryParam("sort", sort.orElse("ASC"))
                .build(Map.of("date", date)))
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .toEntity(EODResponse.class);
        return response.getBody();
    }

}
