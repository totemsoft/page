package com.totemsoft.page.service;

import org.springframework.stereotype.Service;

import com.totemsoft.page.model.entity.Page;
import com.totemsoft.page.repository.PageRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class PageService {

    private final PageRepository repository;

    @Transactional()
    public Page findPage(long pageId) {
        log.trace("findPage({}) ...", pageId);
        return repository.findById(pageId)
            .orElseThrow(() -> new EntityNotFoundException(pageId, Page.class));
    }

}
