package com.totemsoft.page.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.totemsoft.page.model.entity.Key;
import com.totemsoft.page.model.entity.Tag;
import com.totemsoft.page.model.entity.TagType;
import com.totemsoft.page.model.entity.exchangerates.Currency;
import com.totemsoft.page.model.entity.exchangerates.ExchangeRate;
import com.totemsoft.page.model.entity.marketstack.Exchange;
import com.totemsoft.page.model.entity.marketstack.ExchangeTicker;
import com.totemsoft.page.repository.CurrencyRepository;
import com.totemsoft.page.repository.KeyRepository;
import com.totemsoft.page.repository.TagRepository;
import com.totemsoft.page.repository.TagTypeRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
class KeyTaggingService {

    /** exchangeratesapi base currency */
    @Value("${page.exchangeratesapi.io.base-currency}")
    private String baseCurrency;

    private final CurrencyRepository currencyRepository;
    private final KeyRepository keyRepository;
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

    // column/row tagTypes
    List<Tag> findTags(ExchangeRate rate) {
        return List.of(
            findTag(Currency.CURRENCY_BASE, rate.getBase()),
            findTag(Currency.CURRENCY_CODE, rate.getCode()));
    }

    Key saveKey(ExchangeRate rate) {
        final var rateName = rate.getName();
        return keyRepository.findByName(rateName)
            .orElseGet(() -> keyRepository.save(Key.builder()
                .name(rateName)
                .title(rateName)
                .tags(findTags(rate))
                .build()));
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
