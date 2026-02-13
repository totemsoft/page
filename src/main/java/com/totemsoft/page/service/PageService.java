package com.totemsoft.page.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.totemsoft.page.config.SecurityConfig;
import com.totemsoft.page.model.CurrencyDto;
import com.totemsoft.page.model.KeyDto;
import com.totemsoft.page.model.PageDto;
import com.totemsoft.page.model.TagDto;
import com.totemsoft.page.model.TagTypeDto;
import com.totemsoft.page.model.entity.Page;
import com.totemsoft.page.model.entity.SubSection;
import com.totemsoft.page.model.mapper.PageMapper;
import com.totemsoft.page.repository.CurrencyRepository;
import com.totemsoft.page.repository.KeyRepository;
import com.totemsoft.page.repository.KeySpecification;
import com.totemsoft.page.repository.PageRepository;
import com.totemsoft.page.repository.SubSectionRepository;
import com.totemsoft.page.repository.TagRepository;
import com.totemsoft.page.repository.TagTypeRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@PreAuthorize(SecurityConfig.IS_AUTHENTICATED)
@RequiredArgsConstructor
@Log4j2
public class PageService {

    private final CurrencyRepository currencyRepository;

    private final KeyRepository keyRepository;

    private final PageRepository pageRepository;

    private final SubSectionRepository subSectionRepository;

    private final TagRepository tagRepository;

    private final TagTypeRepository tagTypeRepository;

    private final PageMapper pageMapper;

    @Transactional
    public List<CurrencyDto> findBaseCurrencies() {
        final var currencies = currencyRepository.findByBaseTrue();
        return pageMapper.map(currencies);
    }

    @Transactional
    public PageDto findPage(long pageId) {
        final var page = pageRepository.findById(pageId)
            .orElseThrow(() -> new EntityNotFoundException(pageId, Page.class));
        return pageMapper.map(page);
    }

    @Transactional
    public List<PageDto> findPages() {
        log.trace("Getting all available pages ...");
        final var pages = pageRepository.findAll(Sort.by("name"));
        return pageMapper.map(pages);
    }

    @Transactional
    public PageDto findDefaultPage() {
        log.trace("Getting first available page ...");
        final var page = pageRepository.findFirstByOrderByIdAsc()
            .orElseThrow(() -> new EntityNotFoundException(null, Page.class));
        return pageMapper.map(page);
    }

    @Transactional
    public List<TagTypeDto> findTagTypes() {
        final var tagTypes = tagTypeRepository.findAll(Sort.by("title"));
        return pageMapper.mapTagTypes(tagTypes);
    }

    @Transactional
    public List<TagDto> findTags(int tagTypeId) {
        final var tags = tagRepository.findByTagTypeId(tagTypeId);
        return pageMapper.mapTags(tags);
    }

    @Transactional
    public List<TagDto> findTags(int tagTypeId, String name) {
        final var tags = tagRepository.findByTagTypeIdAndNameContainingIgnoreCase(tagTypeId, name);
        return pageMapper.mapTags(tags);
    }

    @Transactional
    public List<KeyDto> findKeys(long subSectionId) {
        final var subSection = subSectionRepository.findById(subSectionId)
            .orElseThrow(() -> new EntityNotFoundException(subSectionId, SubSection.class));
        // all keys from sub-section
        final var keys = subSection.getKeys();
        log.trace("keys: {}", keys);
        return pageMapper.mapKeys(keys);
    }

    @Transactional
    public List<KeyDto> findKeys(Map<Integer, Object> tagTypeMap) {
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
        return pageMapper.mapKeys(keys);
    }

}
