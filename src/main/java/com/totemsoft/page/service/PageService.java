package com.totemsoft.page.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.totemsoft.page.config.RoleEnum;
import com.totemsoft.page.exchangerates.v1.model.CurrencyDto;
import com.totemsoft.page.marketstack.v2.model.ExchangeDto;
import com.totemsoft.page.model.KeyDto;
import com.totemsoft.page.model.PageDto;
import com.totemsoft.page.model.SearchData;
import com.totemsoft.page.model.TagDto;
import com.totemsoft.page.model.TagTypeDto;
import com.totemsoft.page.model.entity.Page;
import com.totemsoft.page.model.entity.SubSection;
import com.totemsoft.page.model.mapper.ExchangeRateMapper;
import com.totemsoft.page.model.mapper.MarketStackMapper;
import com.totemsoft.page.model.mapper.PageMapper;
import com.totemsoft.page.model.mapper.SetupMapper;
import com.totemsoft.page.repository.CurrencyRepository;
import com.totemsoft.page.repository.ExchangeRepository;
import com.totemsoft.page.repository.KeyRepository;
import com.totemsoft.page.repository.KeySpecification;
import com.totemsoft.page.repository.PageRepository;
import com.totemsoft.page.repository.SubSectionRepository;
import com.totemsoft.page.repository.TagRepository;
import com.totemsoft.page.repository.TagTypeRepository;

import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@PreAuthorize(RoleEnum.IS_AUTHENTICATED)
@Transactional
@RequiredArgsConstructor
@Log4j2
public class PageService {

    private final CurrencyRepository currencyRepository;
    private final ExchangeRepository exchangeRepository;
    private final KeyRepository keyRepository;
    private final PageRepository pageRepository;
    private final SubSectionRepository subSectionRepository;
    private final TagRepository tagRepository;
    private final TagTypeRepository tagTypeRepository;

    private final ExchangeRateMapper exchangeRateMapper;
    private final MarketStackMapper marketStackMapper;
    private final PageMapper pageMapper;
    private final SetupMapper setupMapper;

    @PreAuthorize(RoleEnum.PERMIT_ALL)
    @Transactional(value = TxType.SUPPORTS)
    public LocalDate latestDate() {
        return LocalDate.now().minusDays(1);
    }

    public List<CurrencyDto> findBaseCurrencies() {
        final var currencies = currencyRepository.findByBaseTrue();
        return exchangeRateMapper.mapCurrency(currencies);
    }

    public List<ExchangeDto> findBaseExchanges() {
        final var exchanges = exchangeRepository.findByBaseTrue();
        return marketStackMapper.mapExchange(exchanges);
    }

    public PageDto findPage(long pageId) {
        final var page = pageRepository.findById(pageId)
            .orElseThrow(() -> new EntityNotFoundException(pageId, Page.class));
        return pageMapper.map(page);
    }

    public List<PageDto> findPages() {
        log.trace("Getting all available pages ...");
        final var pages = pageRepository.findAll(Sort.by("name"));
        return pageMapper.map(pages);
    }

    public PageDto findDefaultPage() {
        log.trace("Getting first available page ...");
        final var page = pageRepository.findFirstByOrderByIdAsc()
            .orElseThrow(() -> new EntityNotFoundException(null, Page.class));
        return pageMapper.map(page);
    }

    public List<TagTypeDto> findTagTypes() {
        final var tagTypes = tagTypeRepository.findAll(Sort.by("title"));
        return setupMapper.mapTagType(tagTypes);
    }

    public SearchData<TagDto> findTags(Optional<Integer> tagTypeId) {
        return SearchData.<TagDto>builder()
            .records(tagTypeId.isEmpty() ? List.of() : setupMapper.mapTag(tagRepository.findByTagTypeId(tagTypeId.get())))
            .build();
    }

    public SearchData<TagDto> findTags(int tagTypeId, String name) {
        final var tags = tagRepository.findByTagTypeIdAndNameContainingIgnoreCase(tagTypeId, name);
        return SearchData.<TagDto>builder()
            .records(setupMapper.mapTag(tags))
            .build();
    }

    public SearchData<KeyDto> findKeys(long subSectionId) {
        final var subSection = subSectionRepository.findById(subSectionId)
            .orElseThrow(() -> new EntityNotFoundException(subSectionId, SubSection.class));
        // all keys from sub-section
        final var keys = subSection.getKeys();
        log.trace("keys: {}", keys);
        return SearchData.<KeyDto>builder()
            .records(setupMapper.mapKey(keys))
            .build();
    }

    public SearchData<KeyDto> findKeys(Map<Integer, Object> tagTypeMap) {
        final var tagIds = new HashSet<Long>();
        final var tagTitles = new HashMap<Integer, String>();
        tagTypeMap.forEach((tagTypeId, value) -> {
            if (value instanceof Number tagId) {
                tagIds.add(tagId.longValue());
            } else if (value instanceof String tagTitle) {
                tagTitles.put(tagTypeId, tagTitle);
            }
        });
        log.trace("tagIds: {}, tagTitles: {}", tagIds, tagTitles);
        //final var keys = keyRepository.findAll(new KeySpecification(tagIds, tagTitles));
        final var keys = keyRepository.findAll(KeySpecification.findByTagIds(tagIds)
            .and(KeySpecification.findByTagTypeIdAndTagTitles(tagTitles)));
        log.trace("keys: {}", keys);
        return SearchData.<KeyDto>builder()
            .records(setupMapper.mapKey(keys))
            .build();
    }

}
