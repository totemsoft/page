package com.totemsoft.page.exchangerates.v1.api;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.totemsoft.page.exchangerates.v1.model.ExchangeRateSymbols;
import com.totemsoft.page.exchangerates.v1.model.ExchangeRates;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class ExchangeRatesApi {

    @Value("${page.exchangeratesapi.io.access-key}")
    private String accessKey;

    /** exchangeratesapi base currency */
    @Value("${page.exchangeratesapi.io.base-currency}")
    private String baseCurrency;

    private final RestClient exchangeRatesApiRestClient;

    public ExchangeRateSymbols symbols() {
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

    public ExchangeRates latestRates(Optional<String> symbols) {
        log.debug(">>> loading latestRates for base: {}, symbols: {}", baseCurrency, symbols);
        final var response = exchangeRatesApiRestClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/latest")
                .queryParam("access_key", accessKey)
                .queryParam("base", baseCurrency)
                .queryParam("symbols", symbols.orElse(""))
                .build())
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .toEntity(ExchangeRates.class);
        return response.getBody();
    }

    public ExchangeRates historicalRates(LocalDate date, Optional<String> symbols) {
        log.debug(">>> loading historicalRates for date: {}, base: {}, symbols: {}", date, baseCurrency, symbols);
        final var response = exchangeRatesApiRestClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/{date}")
                .queryParam("access_key", accessKey)
                .queryParam("base", baseCurrency)
                .queryParam("symbols", symbols.orElse(""))
                .build(date))
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .toEntity(ExchangeRates.class);
        return response.getBody();
    }

}
