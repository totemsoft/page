package com.totemsoft.page.service;

import org.springframework.stereotype.Service;

import com.totemsoft.page.model.PageDto;
import com.totemsoft.page.model.entity.Page;
import com.totemsoft.page.model.mapper.PageMapper;
import com.totemsoft.page.repository.PageRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class PageService {

    private final PageRepository repository;

    private final PageMapper mapper;

    @Transactional
    public PageDto findPage(long pageId) {
        log.trace("findPage({}) ...", pageId);
        final var page = repository.findById(pageId)
            .orElseThrow(() -> new EntityNotFoundException(pageId, Page.class));
        return mapper.map(page);
    }

}
