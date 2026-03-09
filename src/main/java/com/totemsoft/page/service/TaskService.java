package com.totemsoft.page.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Optional;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.totemsoft.page.exchangerates.v1.api.ExchangeRatesApi;
import com.totemsoft.page.marketstack.v2.api.MarketStackApi;
import com.totemsoft.page.model.entity.SeriesData;
import com.totemsoft.page.model.entity.marketstack.ExchangeTicker;
import com.totemsoft.page.repository.EODBarRepository;
import com.totemsoft.page.repository.KeyRepository;
import com.totemsoft.page.repository.SeriesDataRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Component
@RequiredArgsConstructor
@Log4j2
class TaskService {

    private static final int LIMIT = 1000;

    /** exchangeratesapi base currency */
    @Value("${page.exchangeratesapi.io.base-currency}")
    private String baseCurrency;

    private final ExchangeRatesApi exchangeRatesApi;
    private final MarketStackApi marketStackApi;

    private final ExchangeRateService exchangeRateService;
    private final KeyTaggingService keyTaggingService;
    private final MarketStackService marketStackService;
    private final PageService pageService;

    private final EODBarRepository eodBarRepository;
    private final KeyRepository keyRepository;
    private final SeriesDataRepository seriesDataRepository;

    @Scheduled(cron = "@daily") // @midnight
    @Scheduled(initialDelay = 5_000) // one-time
    void exchangeRateTask() {
        log.info(">>> exchangeRateTask started at: {}", LocalTime.now());
        try {
            // retrieve currencies via API (one page default currency EUR is loaded via data.sql)
            if (exchangeRateService.countCurrencies() < 2) {
                final var symbols = exchangeRatesApi.symbols();
                exchangeRateService.saveCurrencies(symbols.getSymbols());
                keyTaggingService.saveCurrencyTags();
            }
            //
            final var date = pageService.latestDate();
            if (exchangeRateService.existsByDateExchangeRate(date)) {
                log.info("<<< exchangeRates already loaded for: {}", date);
                return;
            }
            // retrieve rates for currencies via API
            final var exchangeRates = exchangeRatesApi.historicalRates(date, Optional.<String>empty());
            // save rates to DB
            final var rates = exchangeRateService.saveExchangeRates(exchangeRates);
            // create key/tag/tagTypes and seriesData
            keyTaggingService.saveSeriesDataKeys(rates);
            log.info("<<< exchangeRateTask completed at: {}", LocalTime.now());
        } catch (Throwable ignore) {
            log.warn("<<< exchangeRateTask failed:", ignore);
        }
    }

    @Scheduled(cron = "@daily") // @midnight
    //@Scheduled(initialDelay = 10_000) // one-time
    void marketStackTask() {
        log.info(">>> marketStackTask started at: {}", LocalTime.now());
        final var date = pageService.latestDate();
        final var dayOfWeek = date.getDayOfWeek();
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            log.info("<<< marketStackTask skiped for {}: {}", dayOfWeek, date);
            return;
        }
        try {
            // retrieve exchanges via API
            final var total = marketStackService.countExchanges(); // pagination.total=2817
            if (total < 2817) {
                final var response = marketStackApi.exchanges(
                    Optional.of(LIMIT), Optional.of(total), Optional.empty());
                log.debug(">>> exchanges found: {}", response.getPagination());
                marketStackService.saveExchanges(response.getData());
            } else {
                log.debug("<<< marketStackTask exchanges already loaded");
            }
            // save tickers/EOD for selected base exchanges
            final var mics = marketStackService.findExchangeBaseMic();
            //log.debug(">>> saving exchangeTickers for: {}", mics);
            //mics.forEach(mic -> marketStackService.saveExchangeTickers(mic, LIMIT, 0));
            // save eodBars for selected base tickers
            log.debug(">>> saving exchangeTickers EOD for: {}", mics);
            mics.forEach(this::saveExchangeTickersEOD);
            //
            log.info("<<< marketStackTask completed at: {}", LocalTime.now());
        } catch (ApiException ignore) {
            // marketStackApi error will be logged in RestClient.defaultStatusHandler
        } catch (Throwable ignore) {
            log.warn("<<< marketStackTask failed:", ignore);
        }
    }

    private void saveExchangeTickersEOD(String mic) {
        final var date = pageService.latestDate();
        final var instant = date.atStartOfDay(ZoneId.systemDefault()).toInstant();
        final var total = 0; // marketStackService.countExchangeTickersEOD(mic, instant);
        if (eodBarRepository.existsByExchangeAndDateAfter(mic, instant)) {
            log.info("<<< eodBar(s) already loaded for: {}, {}", mic, date);
            return;
        }
        final var tickers = marketStackService.findExchangeTickersBase(mic);
        if (tickers.isEmpty()) {
            log.info("<<< no tickers found for: {}", mic);
            return;
        }
        final var symbols = tickers.stream().map(ExchangeTicker::getSymbol).toList();
        try {
            final var response = marketStackApi.eodDate(date, Optional.of(mic), symbols,
                Optional.of(LIMIT), Optional.of(total), Optional.empty());
            final var pagination = response.getPagination();
            log.debug(">>> eodBars found: {}, {}, {}, {}", mic, date, symbols, pagination);
            if (pagination.getCount() > 0) {
                marketStackService.saveExchangeTickersEOD(response.getData());
                marketStackService.saveExchangeTickersEODTags(mic, instant);
            }
        } catch (ApiException ignore) {
            // marketStackApi error will be logged in RestClient.defaultStatusHandler
        }
    }

    @Scheduled(cron = "@daily") // @midnight
    @Scheduled(initialDelay = 15_000) // one-time
    @Transactional
    void seriesDataTask() {
        log.info(">>> seriesDataTask started at: {}", LocalTime.now());
        try {
            final var date = pageService.latestDate();
            final var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
            final var keys = keyRepository.findAll(pageable).getContent();
            if (!seriesDataRepository.findByDateAndKeyIn(date, keys).isEmpty()) {
                log.info("<<< seriesData already loaded for: {}", date);
                return;
            }
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
