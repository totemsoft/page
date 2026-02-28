package com.totemsoft.page.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.totemsoft.page.exchangerates.v1.model.ExchangeRates;
import com.totemsoft.page.model.entity.exchangerates.Currency;
import com.totemsoft.page.model.entity.exchangerates.ExchangeRate;
import com.totemsoft.page.repository.CurrencyRepository;
import com.totemsoft.page.repository.ExchangeRateRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
class ExchangeRateService {

    /** exchangeratesapi base currency */
    @Value("${page.exchangeratesapi.io.base-currency}")
    private String baseCurrency;

    private final CurrencyRepository currencyRepository;
    private final ExchangeRateRepository exchangeRateRepository;

    int countCurrencies() {
        return (int) currencyRepository.count();
    }

    void saveCurrencies(Map<String, String> symbols) {
        log.info(">>> saving {} symbols ...", symbols.size());
        symbols.forEach((code, title) -> currencyRepository.save(Currency.builder()
            .code(code)
            .title(title)
            .build())
        );
    }

    boolean existsByDateExchangeRate(LocalDate date) {
        return exchangeRateRepository.existsByDate(date);
    }

    List<ExchangeRate> saveExchangeRates(ExchangeRates exchangeRates) {
        final var base = exchangeRates.getBase();
        final var rates = exchangeRates.getRates();
        log.info(">>> saving {} exchangeRates for {} [{}] ...", rates.size(), base, baseCurrency);
        final var timestamp = Timestamp.from(Instant.ofEpochMilli(exchangeRates.getTimestamp()));
        final var date = exchangeRates.getDate();
        final var result = new ArrayList<ExchangeRate>(rates.size());
        rates.forEach((code, rate) -> {
            if (rate == null || rate.signum() == 0) {
                log.warn("Zero rate for {}: {}", code, rate);
                //return value;
            }
            final var exchangeRate = exchangeRateRepository.save(ExchangeRate.builder()
                .code(code)
                .date(date)
                .rate(rate)
                .timestamp(timestamp)
                .base(base) // baseCurrency
                .build());
            result.add(exchangeRate);
        });
        return result;
    }

}
