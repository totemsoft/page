package com.totemsoft.page.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.totemsoft.page.config.SecurityConfig;
import com.totemsoft.page.exchangerates.v1.model.CurrencyDto;
import com.totemsoft.page.marketstack.v2.model.ExchangeDto;
import com.totemsoft.page.model.ColumnDef.DropdownOption;
import com.totemsoft.page.model.KeyDto;
import com.totemsoft.page.model.TagDto;
import com.totemsoft.page.model.TagTypeDto;
import com.totemsoft.page.model.entity.Key;
import com.totemsoft.page.model.entity.KeyTag;
import com.totemsoft.page.model.entity.Tag;
import com.totemsoft.page.model.entity.TagType;
import com.totemsoft.page.model.entity.exchangerates.Currency;
import com.totemsoft.page.model.entity.marketstack.Exchange;
import com.totemsoft.page.model.mapper.DropdownOptionMapper;
import com.totemsoft.page.model.mapper.MarketStackMapper;
import com.totemsoft.page.model.mapper.PageMapper;
import com.totemsoft.page.model.mapper.SetupMapper;
import com.totemsoft.page.repository.CurrencyRepository;
import com.totemsoft.page.repository.ExchangeRepository;
import com.totemsoft.page.repository.KeyRepository;
import com.totemsoft.page.repository.KeyTagRepository;
import com.totemsoft.page.repository.TagRepository;
import com.totemsoft.page.repository.TagTypeRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@PreAuthorize(SecurityConfig.HAS_ROLE_SETUP)
@RequiredArgsConstructor
@Log4j2
public class SetupService {

    private final DropdownOptionMapper dropdownOptionMapper;
    private final MarketStackMapper marketStackMapper;
    private final PageMapper pageMapper;
    private final SetupMapper setupMapper;

    private final CurrencyRepository currencyRepository;
    private final ExchangeRepository exchangeRepository;
    private final KeyRepository keyRepository;
    private final KeyTagRepository keyTagRepository;
    private final TagRepository tagRepository;
    private final TagTypeRepository tagTypeRepository;

    @Transactional
    public Long saveTag(TagDto dto) {
        log.trace("saving: {}", dto);
        final var id = dto.getId();
        Tag entity;
        if (id == null) {
            entity = pageMapper.map(dto);
        } else {
            entity = tagRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id, Tag.class));
            entity.setName(dto.getName());
            entity.setTitle(dto.getTitle());
        }
        entity = tagRepository.save(entity);
        return entity.getId();
    }

    @Transactional
    public Integer saveTagType(TagTypeDto dto) {
        log.trace("saving: {}", dto);
        final var id = dto.getId();
        TagType entity;
        if (id == null) {
            entity = pageMapper.map(dto);
        } else {
            entity = tagTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id, TagType.class));
            entity.setName(dto.getName());
            entity.setTitle(dto.getTitle());
        }
        entity = tagTypeRepository.save(entity);
        return entity.getId();
    }

    @Transactional
    public List<TagTypeDto> findTagTypes() {
        final var tagTypes = tagTypeRepository.findAll(Sort.by("title"));
        return pageMapper.mapTagTypes(tagTypes);
    }

    @Transactional
    public List<DropdownOption> tagTypeDropdownOptions() {
        final var tagTypes = tagTypeRepository.findAll(Sort.by("title"));
        return dropdownOptionMapper.map(tagTypes);
    }

    @Transactional
    public List<TagDto> findTagsByKey(long keyId) {
        final var key = keyRepository.findById(keyId)
            .orElseThrow(() -> new EntityNotFoundException(keyId, Key.class));
        return setupMapper.map(key).getTags();
    }

    @Transactional
    public List<KeyDto> findKeys() {
        final var keys = keyRepository.findAll(Sort.by("title"));
        return setupMapper.map(keys);
    }

    @Transactional
    public Long saveKey(KeyDto dto) {
        log.trace("saving: {}", dto);
        final var id = dto.getId();
        Key entity;
        if (id == null) {
            entity = setupMapper.map(dto);
        } else {
            entity = keyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id, Key.class));
            entity.setName(dto.getName());
            entity.setTitle(dto.getTitle());
        }
        entity = keyRepository.save(entity);
        return entity.getId();
    }

    @Transactional
    public void saveKeyTags(long keyId, List<Long> tagIds) {
        keyTagRepository.deleteAllByKeyId(keyId);
        tagIds.forEach(tagId -> keyTagRepository.save(
            KeyTag.builder()
                .keyId(keyId)
                .tagId(tagId)
                .build()
            )
        );
    }

    @Transactional
    public List<CurrencyDto> findCurrencies() {
        final var currencies = currencyRepository.findAll(
            Sort.by("base").descending().and(Sort.by("code")));
        return pageMapper.map(currencies);
    }

    @Transactional
    public String saveCurrency(CurrencyDto dto) {
        log.trace("saving: {}", dto);
        final var code = dto.getCode();
        var entity = currencyRepository.findById(code)
            .orElseThrow(() -> new EntityNotFoundException(code, Currency.class));
        entity.setBase(dto.getBase());
        entity = currencyRepository.save(entity);
        return entity.getCode();
    }

    @Transactional
    public List<ExchangeDto> findExchanges() {
        final var exchanges = exchangeRepository.findAll(
            Sort.by("base").descending().and(Sort.by("city")).and(Sort.by("mic")));
        return marketStackMapper.map(exchanges);
    }

    @Transactional
    public String saveExchange(ExchangeDto dto) {
        log.trace("saving: {}", dto);
        final var mic = dto.getMic();
        var entity = exchangeRepository.findById(mic)
            .orElseThrow(() -> new EntityNotFoundException(mic, Exchange.class));
        entity.setBase(dto.getBase());
        entity = exchangeRepository.save(entity);
        return entity.getMic();
    }

}
