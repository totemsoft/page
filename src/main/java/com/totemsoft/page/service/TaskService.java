package com.totemsoft.page.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.totemsoft.page.exchangerates.v1.api.ExchangeRatesApi;
import com.totemsoft.page.marketstack.v2.api.MarketStackApi;
import com.totemsoft.page.model.entity.SeriesData;
import com.totemsoft.page.repository.KeyRepository;
import com.totemsoft.page.repository.SeriesDataRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Component
@RequiredArgsConstructor
@Log4j2
public class TaskService {

    /** exchangeratesapi base currency */
    @Value("${page.exchangeratesapi.io.base-currency}")
    private String baseCurrency;

    private final ExchangeRatesApi exchangeRatesApi;

    private final MarketStackApi marketStackApi;

    private final ExchangeRateService exchangeRateService;

    private final MarketStackService marketStackService;

    private final KeyRepository keyRepository;

    private final SeriesDataRepository seriesDataRepository;

    @Scheduled(cron = "@daily") // @midnight
    @Scheduled(initialDelay = 5_000) // one-time
    public void exchangeRateTask() {
        log.info(">>> exchangeRateTask started at: {}", LocalTime.now());
        try {
            // retrieve currencies via API (one page default currency EUR is loaded via data.sql)
            if (exchangeRateService.countCurrencies() < 2) {
                final var symbols = exchangeRatesApi.symbols();
                exchangeRateService.saveCurrencies(symbols.getSymbols());
                exchangeRateService.saveCurrencyTags();
            }
            exchangeRateService.saveCurrencyTags();
            //
            final var date = LocalDate.now().minusDays(1);
            if (exchangeRateService.existsByDateExchangeRate(date)) {
                log.info("<<< exchangeRates already loaded for: {}", date);
                return;
            }
            // retrieve rates for currencies via API
            final var exchangeRates = exchangeRatesApi.historicalRates(date, Optional.<String>empty());
            // save rates to DB
            final var rates = exchangeRateService.saveExchangeRates(exchangeRates);
            // create key/tag/tagTypes and seriesData
            rates.forEach(exchangeRateService::saveSeriesDataKey);
            log.info("<<< exchangeRateTask completed at: {}", LocalTime.now());
        } catch (Throwable ignore) {
            log.warn("<<< exchangeRateTask failed:", ignore);
        }
    }

    @Scheduled(cron = "@daily") // @midnight
    @Scheduled(initialDelay = 10_000) // one-time
    public void marketStackTask() {
        log.info(">>> marketStackTask started at: {}", LocalTime.now());
        try {
            // retrieve exchanges via API
            final var LIMIT = 1000;
            final var total = marketStackService.countExchanges(); // pagination.total=2817
            if (total < 2817) {
                final var response = marketStackApi.exchanges(Optional.of(LIMIT), Optional.of(total), Optional.empty());
                log.debug(">>> marketStackTask exchanges found: {}", response.getPagination());
                marketStackService.saveExchanges(response.getData());
            } else {
                log.debug("<<< marketStackTask exchanges already loaded");
            }
            // save tickers for selected base exchanges
            final var mics = marketStackService.findExchangeBaseMic();
            log.debug(">>> saving exchangeTickers for: {}", mics);
            mics.forEach(this::saveExchangeTicker);

            log.info("<<< marketStackTask completed at: {}", LocalTime.now());
        } catch (Throwable ignore) {
            log.warn("<<< marketStackTask failed:", ignore);
        }
    }

    private void saveExchangeTicker(String mic) {
        final var LIMIT = 1000;
        final var total = marketStackService.countExchangeTickers(mic);
        // XNAS total=45173 (NASDAQ - ALL MARKETS)
        if (total % LIMIT != 0) {
            log.debug("<<< {} exchangeTickers already loaded for: {}", total, mic);
            return;
        }
        final var response = marketStackApi.exchangeTickers(mic, Optional.of(LIMIT), Optional.of(total));
        log.debug(">>> marketStackTask exchangeTickers found: {} {}", mic, response.getPagination());
        marketStackService.saveExchangeTickers(mic, response.getData().getTickers());
    }

    @Scheduled(cron = "@daily") // @midnight
    @Scheduled(initialDelay = 15_000) // one-time
    @Transactional
    void seriesDataTask() {
        log.info(">>> seriesDataTask started at: {}", LocalTime.now());
        try {
            final var date = LocalDate.now();
            if (seriesDataRepository.existsByDate(date)) {
                log.info("<<< seriesData already loaded for: {}", date);
                return;
            }
            final var pageable = PageRequest.of(0, 10);
            final var keys = keyRepository.findAll(pageable).getContent();
            keys.forEach(key -> {
                final var d = saveSeriesData(key.getId(), date);
                log.trace("seriesData saved: {}", d);
            });
            log.info("<<< seriesDataTask completed at: {}", LocalTime.now());
        } catch (Throwable ignore) {
            log.warn("<<< seriesDataTask failed:", ignore);
        }
    }

    private SeriesData saveSeriesData(long keyId, LocalDate date) {
        return seriesDataRepository.save(SeriesData.builder()
            .keyId(keyId)
            .date(date)
            .value(randomValue(1_000, 1_000_000))
            .currency(baseCurrency)
            .baseCurrency(baseCurrency)
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
