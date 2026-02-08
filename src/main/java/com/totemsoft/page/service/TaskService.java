package com.totemsoft.page.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.totemsoft.page.model.entity.Currency;
import com.totemsoft.page.model.entity.ExchangeRate;
import com.totemsoft.page.model.entity.Key;
import com.totemsoft.page.model.entity.SeriesData;
import com.totemsoft.page.model.entity.Tag;
import com.totemsoft.page.model.entity.TagType;
import com.totemsoft.page.model.exchange.ExchangeRates;
import com.totemsoft.page.repository.CurrencyRepository;
import com.totemsoft.page.repository.ExchangeRateRepository;
import com.totemsoft.page.repository.KeyRepository;
import com.totemsoft.page.repository.SeriesDataRepository;
import com.totemsoft.page.repository.TagRepository;
import com.totemsoft.page.repository.TagTypeRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Component
@RequiredArgsConstructor
@Log4j2
public class TaskService {

    /** default page currency */
    @Value("${page.currency}")
    private String currency;

    /** exchangeratesapi base currency */
    @Value("${page.exchangeratesapi.io.base-currency}")
    private String baseCurrency;

    private final CurrencyRepository currencyRepository;

    private final ExchangeRateRepository exchangeRateRepository;

    private final ExchangeRateService exchangeRateService;

    private final KeyRepository keyRepository;

    private final SeriesDataRepository seriesDataRepository;

    private final TagRepository tagRepository;

    private final TagTypeRepository tagTypeRepository;

    @Scheduled(cron = "@daily") // @midnight
    @Scheduled(initialDelay = 5_000) // one-time
    @Transactional
    public void exchangeRateTask() {
        log.info(">>> exchangeRateTask for {} started at: {}", baseCurrency, LocalTime.now());
        try {
            // retrieve currencies via API (one page default currency EUR is loaded via data.sql)
            if (currencyRepository.count() < 2) {
                final var symbols = exchangeRateService.symbols();
                saveCurrencies(symbols.getSymbols());
            }
            final var date = LocalDate.now().minusDays(1);
            if (exchangeRateRepository.existsByDate(date)) {
                log.info("<<< exchangeRates already loaded for: {}", date);
                return;
            }
            // retrieve currencies from DB
            //final var currencies = currencyRepository.findAll().stream().map(Currency::getCode).toList();
            //final var symbols = Optional.of(String.join(",", currencies));
            // retrieve rates for currencies via API
            final var symbols = Optional.<String>empty(); // String.join(",", currencies);
            final var exchangeRates = exchangeRateService.historicalRates(date, baseCurrency, symbols);
            if (baseCurrency.compareTo(exchangeRates.getBase()) != 0) {
                log.error("ERROR: {} != {}", baseCurrency, exchangeRates.getBase());
                return;
            }
            if (date.compareTo(exchangeRates.getDate()) != 0) {
                log.error("ERROR: {} != {}", date, exchangeRates.getDate());
                return;
            }
            // save rates to DB
            final var rates = saveExchangeRates(exchangeRates);
            // create key/tag/tagTypes and seriesData
            rates.forEach(this::saveSeriesDataKey);
            log.info("<<< exchangeRateTask executed at: {}", LocalTime.now());
        } catch (Throwable ignore) {
            log.warn("<<< exchangeRateTask failed:", ignore);
        }
    }

    private List<ExchangeRate> saveExchangeRates(ExchangeRates exchangeRates) {
        final var rates = exchangeRates.getRates();
        log.info(">>> saving {} exchangeRates ...", rates.size());
        final var timestamp = Timestamp.from(Instant.ofEpochMilli(exchangeRates.getTimestamp()));
        final var date = exchangeRates.getDate();
        final var result = new ArrayList<ExchangeRate>(rates.size());
        rates.forEach((code, rate) -> {
            final var exchangeRate = exchangeRateRepository.save(ExchangeRate.builder()
                .code(code)
                .date(date)
                .rate(rate)
                .timestamp(timestamp)
                .baseCurrency(baseCurrency)
                .build());
            result.add(exchangeRate);
        });
        return result;
    }

    private void saveCurrencies(Map<String, String> symbols) {
        log.info(">>> saving {} symbols ...", symbols.size());
        symbols.forEach((code, title) -> currencyRepository.save(
            Currency.builder()
                .code(code)
                .title(title)
                .build())
        );
    }

    private SeriesData saveSeriesDataKey(ExchangeRate rate) {
        final var rateName = rate.getName();
        final var key = keyRepository.findByName(rateName)
            .orElseGet(() -> keyRepository.save(Key.builder()
                .name(rateName)
                .title(rateName)
                .tags(saveTags(rate))
                .build()));
        final var date = rate.getDate();
        final long keyId = key.getId();
        return seriesDataRepository.findByDateAndKeyId(date, keyId)
            .orElseGet(() -> seriesDataRepository.save(SeriesData.builder()
                .keyId(keyId)
                .date(date)
                .value(rate.getRate())
                .currency(rate.getCode())
                .baseCurrency(rate.getBaseCurrency())
                .title(rateName)
                .build()));
    }

    private List<Tag> saveTags(ExchangeRate rate) {
        // column/row tagTypes
        return List.of(
            saveTag(ExchangeRate.CURRENCY_NAME, rate.getName()),
            saveTag(ExchangeRate.CURRENCY_RATE, rate.getName()));
    }

    private Tag saveTag(String tagTypeName, String tagName) {
        final var tagType = tagTypeRepository.findByName(tagTypeName)
            .orElseGet(() -> tagTypeRepository.save(TagType.builder()
                .name(tagTypeName)
                .title(tagTypeName.toLowerCase().replace('_', ' '))
                .build()));
        final int tagTypeId = tagType.getId();
        log.debug("tagType id: {}, name: {}", tagTypeId, tagTypeName);
        return tagRepository.findByTagTypeIdAndName(tagTypeId, tagName)
            .orElseGet(() -> tagRepository.save(Tag.builder()
                .tagTypeId(tagTypeId)
                .name(tagName)
                .title(tagName)
                .build()));
    }

    @Scheduled(cron = "@daily") // @midnight
    @Scheduled(initialDelay = 10_000) // one-time
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
            log.info("<<< seriesDataTask executed at: {}", LocalTime.now());
        } catch (Throwable ignore) {
            log.warn("<<< seriesDataTask failed:", ignore);
        }
    }

    private SeriesData saveSeriesData(long keyId, LocalDate date) {
        return seriesDataRepository.save(SeriesData.builder()
            .keyId(keyId)
            .date(date)
            .value(randomValue(1_000, 1_000_000))
            .currency(currency)
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
