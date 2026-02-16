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
import com.totemsoft.page.model.entity.Key;
import com.totemsoft.page.model.entity.SeriesData;
import com.totemsoft.page.model.entity.Tag;
import com.totemsoft.page.model.entity.TagType;
import com.totemsoft.page.model.entity.exchangerates.Currency;
import com.totemsoft.page.model.entity.exchangerates.ExchangeRate;
import com.totemsoft.page.repository.CurrencyRepository;
import com.totemsoft.page.repository.ExchangeRateRepository;
import com.totemsoft.page.repository.KeyRepository;
import com.totemsoft.page.repository.SeriesDataRepository;
import com.totemsoft.page.repository.TagRepository;
import com.totemsoft.page.repository.TagTypeRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
class ExchangeRateService {

    /** exchangeratesapi base currency */
    @Value("${page.exchangeratesapi.io.base-currency}")
    private String baseCurrency;

    private final CurrencyRepository currencyRepository;

    private final ExchangeRateRepository exchangeRateRepository;

    private final KeyRepository keyRepository;

    private final SeriesDataRepository seriesDataRepository;

    private final TagRepository tagRepository;

    private final TagTypeRepository tagTypeRepository;

    @Transactional
    int countCurrencies() {
        return (int) currencyRepository.count();
    }

    @Transactional
    void saveCurrencies(Map<String, String> symbols) {
        log.info(">>> saving {} symbols ...", symbols.size());
        symbols.forEach((code, title) -> currencyRepository.save(
            Currency.builder()
                .code(code)
                .title(title)
                .build())
        );
    }

    @Transactional
    boolean existsByDateExchangeRate(LocalDate date) {
        return exchangeRateRepository.existsByDate(date);
    }

    @Transactional
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
                .base(baseCurrency)
                .build());
            result.add(exchangeRate);
        });
        return result;
    }

    @Transactional
    SeriesData saveSeriesDataKey(ExchangeRate rate) {
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
                .baseCurrency(rate.getBase())
                .title(rateName)
                .build()));
    }

    private List<Tag> saveTags(ExchangeRate rate) {
        // column/row tagTypes
        return List.of(
            saveTag(ExchangeRate.CURRENCY_BASE, rate.getBase()),
            saveTag(ExchangeRate.CURRENCY_CODE, rate.getCode()));
    }

    private Tag saveTag(String tagTypeName, String tagName) {
        final var tagType = tagTypeRepository.findByName(tagTypeName)
            .orElseGet(() -> tagTypeRepository.save(TagType.builder()
                .name(tagTypeName)
                .title(tagTypeName.toLowerCase().replace('_', ' '))
                .build()));
        final int tagTypeId = tagType.getId();
        return tagRepository.findByTagTypeIdAndName(tagTypeId, tagName)
            .orElseGet(() -> tagRepository.save(Tag.builder()
                .tagTypeId(tagTypeId)
                .name(tagName)
                .title(tagName)
                .build()));
    }

}
