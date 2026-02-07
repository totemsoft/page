package com.totemsoft.page.service;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.totemsoft.page.model.exchange.ExchangeRateSymbols;
import com.totemsoft.page.model.exchange.ExchangeRates;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
class ExchangeRateService {

    private final String accessKey;

    private final RestClient exchangeRatesApiRestClient;

    public ExchangeRateService(
            @Value("${page.exchangeratesapi.io.access-key}") String accessKey,
            RestClient exchangeRatesApiRestClient) {
        this.accessKey = accessKey;
        this.exchangeRatesApiRestClient = exchangeRatesApiRestClient;
    }

    ExchangeRateSymbols symbols() {
        final var response = exchangeRatesApiRestClient.get()
            .uri(uriBuilder -> uriBuilder
                    .path("/symbols")
                    .queryParam("access_key", accessKey)
                    .build())
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .toEntity(ExchangeRateSymbols.class);
        return response.getBody();
    }

    ExchangeRates latestRates(String base, Optional<String> symbols) {
        final var response = exchangeRatesApiRestClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/latest")
                .queryParam("base", base)
                .queryParam("symbols", symbols.orElse(""))
                .queryParam("access_key", accessKey)
                .build())
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .toEntity(ExchangeRates.class);
        return response.getBody();
    }

    ExchangeRates historicalRates(LocalDate date, String base, Optional<String> symbols) {
        log.debug(">>> loading exchangeRates for date: {}, base: {}, symbols: {}", date, base, symbols);
        final var response = exchangeRatesApiRestClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/{date}")
                .queryParam("base", base)
                .queryParam("symbols", symbols.orElse(""))
                .queryParam("access_key", accessKey)
                .build(date))
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .toEntity(ExchangeRates.class);
        return response.getBody();
    }

}
