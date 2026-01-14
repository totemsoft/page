package com.totemsoft.page.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.totemsoft.page.model.KeyDto;
import com.totemsoft.page.model.PageDto;
import com.totemsoft.page.model.TagDto;
import com.totemsoft.page.model.TagTypeDto;
import com.totemsoft.page.model.entity.Key;
import com.totemsoft.page.model.entity.Page;
import com.totemsoft.page.model.entity.SubSection;
import com.totemsoft.page.model.mapper.PageMapper;
import com.totemsoft.page.repository.KeyTagRepository;
import com.totemsoft.page.repository.PageRepository;
import com.totemsoft.page.repository.SubSectionRepository;
import com.totemsoft.page.repository.TagRepository;
import com.totemsoft.page.repository.TagTypeRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class PageService {

    private final KeyTagRepository keyTagRepository;

    private final PageRepository pageRepository;

    private final SubSectionRepository subSectionRepository;

    private final TagRepository tagRepository;

    private final TagTypeRepository tagTypeRepository;

    private final PageMapper mapper;

    @Transactional
    public PageDto findPage(long pageId) {
        final var page = pageRepository.findById(pageId)
            .orElseThrow(() -> new EntityNotFoundException(pageId, Page.class));
        return mapper.map(page);
    }

    @Transactional
    public List<TagTypeDto> findTagTypes() {
        final var tagTypes = tagTypeRepository.findAll(Sort.by("title"));
        return mapper.mapTagTypes(tagTypes);
    }

    @Transactional
    public List<TagDto> findTags(int tagTypeId, String title) {
        final var tags = tagRepository.findByTagTypeIdAndTitleContainingIgnoreCase(tagTypeId, title);
        return mapper.mapTags(tags);
    }

    @Transactional
    public List<KeyDto> findKeys(long subSectionId) {
        final var subSection = subSectionRepository.findById(subSectionId)
            .orElseThrow(() -> new EntityNotFoundException(subSectionId, SubSection.class));
        // all keys from sub-section
        final var keys = subSection.getKeys();
        log.trace("keys: {}", keys);
        return mapper.mapKeys(keys);
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
        final var keys = new ArrayList<Key>();
        if (!tagIds.isEmpty()) {
            keys.addAll(keyTagRepository.findByTagIdIn(tagIds));
        }
        if (!tagTitles.isEmpty()) {
            tagTitles.forEach((tagTypeId, tagTitle) -> keys.addAll(
                keyTagRepository.findByTagTypeIdAndTagTitleContainingIgnoreCase(tagTypeId, '%' + tagTitle.trim() + '%')
            ));
        }
        log.trace("keys: {}", keys);
        return mapper.mapKeys(keys);
    }

}
