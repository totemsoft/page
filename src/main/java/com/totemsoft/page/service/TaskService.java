package com.totemsoft.page.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.totemsoft.page.model.entity.Currency;
import com.totemsoft.page.model.entity.SeriesData;
import com.totemsoft.page.repository.CurrencyRepository;
import com.totemsoft.page.repository.KeyRepository;
import com.totemsoft.page.repository.SeriesDataRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Component
@RequiredArgsConstructor
@Log4j2
public class TaskService {

    @Value("${page.exchangeratesapi.io.base-currency}")
    private String baseCurrency;

    private final CurrencyRepository currencyRepository;

    private final ExchangeRateService exchangeRateService;

    private final KeyRepository keyRepository;

    private final SeriesDataRepository seriesDataRepository;

    @Scheduled(cron = "@daily") // @midnight
    @Scheduled(initialDelay = 5_000) // one-time
    @Transactional
    public void exchangeRateTask() {
        log.info(">>> exchangeRateTask for {} started at: {}", baseCurrency, LocalTime.now());
        try {
            // retrieve from API
            if (!currencyRepository.existsById(baseCurrency)) {
                final var supportedSymbols = exchangeRateService.symbols();
                log.debug("supportedSymbols: {}", supportedSymbols);
                supportedSymbols.getSymbols().forEach((code, title) -> 
                    currencyRepository.save(
                        Currency.builder().code(code).title(title).build()
                    )
                );
            }
            // retrieve from DB
            final var currencies = currencyRepository.findAll().stream()
                .map(Currency::getCode)
                .toList();
            final var date = LocalDate.now().minusDays(1);
            final String symbols = String.join(",", currencies);
            log.debug("date: {}, base: {}, symbols: {}", date, baseCurrency, symbols);
            //final var exchangeRates = exchangeRateService.historicalRates(date, baseCurrency, symbols);
            
            log.info("<<< exchangeRateTask executed at: {}", LocalTime.now());
        } catch (Throwable ignore) {
            log.warn("<<< exchangeRateTask failed:", ignore);
        }
    }

    @Scheduled(cron = "@daily") // @midnight
    @Scheduled(initialDelay = 10_000) // one-time
    @Transactional
    void seriesDataTask() {
        log.info(">>> seriesDataTask started at: {}", LocalTime.now());
        try {
            final var date = LocalDate.now();
            if (seriesDataRepository.existsByDate(date)) {
                log.info("<<< Data already loaded for: {}", date);
                return;
            }
            final var pageable = PageRequest.of(0, 100);
            final var keys = keyRepository.findAll(pageable).getContent();
            keys.forEach(key -> {
                final var d = loadData(key.getId(), date);
                log.trace("saved: {}", d);
            });
            log.info("<<< seriesDataTask executed at: {}", LocalTime.now());
        } catch (Throwable ignore) {
            log.warn("<<< seriesDataTask failed:", ignore);
        }
    }

    private SeriesData loadData(long keyId, LocalDate date) {
        return seriesDataRepository.save(SeriesData.builder()
            .keyId(keyId)
            .date(date)
            .value(randomValue(1_000, 1_000_000))
            .currency(baseCurrency)
            .title(randomTitle(16, 32))
            .build());
    }

    /**
     * Generate the random number: min + (max - min) * randomDouble[0,1]
     */
    private static BigDecimal randomValue(long min, long max) {
        return BigDecimal.valueOf(min)
            .add(BigDecimal.valueOf(max - min)
            .multiply(BigDecimal.valueOf(Math.random())))
            .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Generate the random string whose length is between the inclusive minimum and the exclusive maximum
     */
    private static String randomTitle(int min, int max) {
        return RandomStringUtils.insecure().nextAlphabetic(min, max);
    }

}
