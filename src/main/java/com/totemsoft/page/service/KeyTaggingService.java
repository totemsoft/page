package com.totemsoft.page.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.totemsoft.page.model.entity.Key;
import com.totemsoft.page.model.entity.SeriesData;
import com.totemsoft.page.model.entity.Tag;
import com.totemsoft.page.model.entity.TagType;
import com.totemsoft.page.model.entity.exchangerates.Currency;
import com.totemsoft.page.model.entity.exchangerates.ExchangeRate;
import com.totemsoft.page.model.entity.marketstack.EODBar;
import com.totemsoft.page.model.entity.marketstack.Exchange;
import com.totemsoft.page.model.entity.marketstack.ExchangeTicker;
import com.totemsoft.page.repository.CurrencyRepository;
import com.totemsoft.page.repository.KeyRepository;
import com.totemsoft.page.repository.SeriesDataRepository;
import com.totemsoft.page.repository.TagRepository;
import com.totemsoft.page.repository.TagTypeRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
class KeyTaggingService {

    /** exchangeratesapi base currency */
    @Value("${page.exchangeratesapi.io.base-currency}")
    private String baseCurrency;

    private final CurrencyRepository currencyRepository;
    private final KeyRepository keyRepository;
    private final SeriesDataRepository seriesDataRepository;
    private final TagRepository tagRepository;
    private final TagTypeRepository tagTypeRepository;

    Tag saveTag(String tagTypeName, String tagName, String tagTitle) {
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
                .title(tagTitle)
                .build()));
    }

    Tag findTag(String tagTypeName, String tagName) {
        final var tagType = tagTypeRepository.findByName(tagTypeName)
            .orElseThrow(() -> new EntityNotFoundException(tagTypeName, TagType.class));
        final int tagTypeId = tagType.getId();
        return tagRepository.findByTagTypeIdAndName(tagTypeId, tagName)
            .orElseThrow(() -> new EntityNotFoundException(tagTypeId + ':' + tagName, Tag.class));
    }

    void saveCurrencyTags() {
        // columns tagType
        currencyRepository.findAll().forEach(currency ->
            saveTag(Currency.CURRENCY_CODE, currency.getCode(), currency.getTitle()));
        // row tagType
        final var currency = currencyRepository.findById(baseCurrency)
            .orElseThrow(() -> new EntityNotFoundException(baseCurrency, Currency.class));
        currency.setBase(true);
        currencyRepository.save(currency);
        saveTag(Currency.CURRENCY_BASE, currency.getCode(), currency.getTitle());
    }

    List<Tag> findTags(ExchangeRate entity) {
        return List.of(
            findTag(Currency.CURRENCY_BASE, entity.getBase()),
            findTag(Currency.CURRENCY_CODE, entity.getCode()));
    }

    Key saveKey(ExchangeRate entity) {
        final var keyName = entity.getKeyName();
        return keyRepository.findByName(keyName)
            .orElseGet(() -> keyRepository.save(Key.builder()
                .name(keyName)
                .title(keyName)
                .tags(findTags(entity))
                .build()));
    }

    SeriesData saveSeriesDataKey(ExchangeRate entity) {
        final var key = saveKey(entity);
        final var date = entity.getDate();
        final long keyId = key.getId();
        return seriesDataRepository.findByDateAndKeyId(date, keyId)
            .orElseGet(() -> seriesDataRepository.save(SeriesData.builder()
                .keyId(keyId)
                .date(date)
                .value(entity.getRate())
                .currency(entity.getCode())
                .baseCurrency(entity.getBase())
                .title(entity.getKeyName())
                .build()));
    }

    void saveSeriesDataKeys(List<ExchangeRate> rates) {
        rates.forEach(this::saveSeriesDataKey);
    }

    List<Tag> findTags(EODBar entity) {
        return List.of(
            findTag(Exchange.EXCHANGE, entity.getExchange()),
            findTag(ExchangeTicker.EXCHANGE_TICKER, entity.getSymbol()),
            findTag(EODBar.ASSET_CLASS, EODBar.ASSET_CLASS_STOCK));
    }

    Key saveKey(EODBar entity) {
        final var keyName = entity.getKeyName();
        return keyRepository.findByName(keyName)
            .orElseGet(() -> keyRepository.save(Key.builder()
                .name(keyName)
                .title(keyName)
                .tags(findTags(entity))
                .build()));
    }

    SeriesData saveSeriesDataKey(EODBar entity) {
        final var key = saveKey(entity);
        final var date = LocalDate.ofInstant(entity.getDate(), ZoneId.systemDefault());
        final long keyId = key.getId();
        final var currency = priceCurrency(entity);
        return seriesDataRepository.findByDateAndKeyId(date, keyId)
            .orElseGet(() -> seriesDataRepository.save(SeriesData.builder()
                .keyId(keyId)
                .date(date)
                .value(entity.getClose())
                .currency(currency)
                .baseCurrency(currency)
                .title(entity.getKeyName())
                .build()));
    }

    private String priceCurrency(EODBar entity) {
        final var defaultCurrency = "USD";
        return Optional.ofNullable(entity.getPriceCurrency())
            .orElseGet(() -> currencyRepository.existsById(defaultCurrency) ? defaultCurrency : baseCurrency);
    }

    Tag saveTag(Exchange entity) {
        if (Boolean.TRUE.equals(entity.getBase())) {
            return saveTag(Exchange.EXCHANGE, entity.getMic(), entity.getName());
        }
        return null;
    }

    Tag saveTag(ExchangeTicker entity) {
        if (Boolean.TRUE.equals(entity.getBase())) {
            return saveTag(ExchangeTicker.EXCHANGE_TICKER, entity.getSymbol(), entity.getName());
        }
        return null;
    }

}
