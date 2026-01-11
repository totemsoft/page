package com.totemsoft.page.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.totemsoft.page.model.PageDto;
import com.totemsoft.page.model.TagTypeDto;
import com.totemsoft.page.model.entity.Page;
import com.totemsoft.page.model.mapper.PageMapper;
import com.totemsoft.page.repository.PageRepository;
import com.totemsoft.page.repository.TagTypeRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PageService {

    private final PageRepository repository;

    private final TagTypeRepository tagTypeRepository;

    private final PageMapper mapper;

    @Transactional
    public PageDto findPage(long pageId) {
        final var page = repository.findById(pageId)
            .orElseThrow(() -> new EntityNotFoundException(pageId, Page.class));
        return mapper.map(page);
    }

    public List<TagTypeDto> findTagTypes() {
        final var tagTypes = tagTypeRepository.findAll(Sort.by("title"));
        return mapper.mapTagTypes(tagTypes);
    }

}
