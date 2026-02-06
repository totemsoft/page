package com.totemsoft.page.service;

import java.time.LocalDate;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.totemsoft.page.model.exchange.ExchangeRateSymbols;
import com.totemsoft.page.model.exchange.ExchangeRates;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
class ExchangeRateService {

    private final RestClient exchangeRatesApiRestClient;

    ExchangeRateSymbols symbols() {
        final var response = exchangeRatesApiRestClient.get()
            .uri("/symbols?access_key={access_key}")
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .toEntity(ExchangeRateSymbols.class);
        return response.getBody();
    }

    ExchangeRates latestRates(String currency, String symbols) {
        final var response = exchangeRatesApiRestClient.get()
            .uri("/latest?access_key={access_key}&base={base}&symbols={symbols}")
            .attribute("base", currency) // AUD
            .attribute("symbols", symbols) // USD,GBP,JPY
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .toEntity(ExchangeRates.class);
        return response.getBody();
    }

    ExchangeRates historicalRates(LocalDate date, String currency, String symbols) {
        final var response = exchangeRatesApiRestClient.get()
            .uri("/{date}?access_key={access_key}&base={base}&symbols={symbols}")
            .attribute("date", date.toString()) // yyyy-MM-dd
            .attribute("base", currency) // AUD
            .attribute("symbols", symbols) // USD,GBP,JPY
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .toEntity(ExchangeRates.class);
        return response.getBody();
    }

}
