package com.totemsoft.page.service;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.totemsoft.page.config.SecurityConfig;
import com.totemsoft.page.model.PageDto;
import com.totemsoft.page.model.PageResponse;
import com.totemsoft.page.model.SectionDto;
import com.totemsoft.page.model.SubSectionDto;
import com.totemsoft.page.model.TabDto;
import com.totemsoft.page.model.entity.Page;
import com.totemsoft.page.model.entity.Section;
import com.totemsoft.page.model.entity.SubSection;
import com.totemsoft.page.model.entity.Tab;
import com.totemsoft.page.model.mapper.PageMapper;
import com.totemsoft.page.model.mapper.SetupMapper;
import com.totemsoft.page.repository.PageRepository;
import com.totemsoft.page.repository.SectionRepository;
import com.totemsoft.page.repository.SubSectionRepository;
import com.totemsoft.page.repository.TabRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@PreAuthorize(SecurityConfig.HAS_ROLE_ADMIN_PAGE)
@RequiredArgsConstructor
@Log4j2
public class PageStructureService {

    private final PageMapper pageMapper;
    private final SetupMapper setupMapper;

    private final PageRepository pageRepository;
    private final SectionRepository sectionRepository;
    private final SubSectionRepository subSectionRepository;
    private final TabRepository tabRepository;

    @Transactional
    public PageResponse savePage(final PageDto pageDto) {
        log.trace("saving: {}", pageDto);
        final var pageId = pageDto.getId();
        Page page;
        if (pageId == null) {
            page = pageMapper.map(pageDto);
        } else {
            page = pageRepository.findById(pageId)
                .orElseThrow(() -> new EntityNotFoundException(pageId, Page.class));
            page.setName(pageDto.getName());
        }
        log.trace("saving: {}", page);
        page = pageRepository.save(page);
        // add first tab to new page
        if (pageId == null) {
            log.trace("created: {}", page);
            final var tab = tabRepository.save(Tab.builder()
                .name("Tab 1")
                .pageId(page.getId())
                .build());
            log.trace("created: {}", tab);
        }
        return PageResponse.builder()
            .pageId(page.getId())
            .build();
    }

    @Transactional
    public void saveTab(final TabDto tabDto) {
        log.trace("saving: {}", tabDto);
        final var tabId = tabDto.getId();
        final Tab tab;
        if (tabId == null) {
            tab = pageMapper.mapTab(tabDto);
        } else {
            tab = tabRepository.findById(tabId)
                .orElseThrow(() -> new EntityNotFoundException(tabId, Tab.class));
            tab.setName(tabDto.getName());
            tab.setIndex(tabDto.getIndex());
        }
        log.trace("saving: {}", tab);
        tabRepository.save(tab);
    }

    @Transactional
    public void saveSection(final SectionDto sectionDto) {
        log.trace("saving: {}", sectionDto);
        final var sectionId = sectionDto.getId();
        final Section section;
        if (sectionId == null) {
            section = pageMapper.mapSection(sectionDto);
        } else {
            section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new EntityNotFoundException(sectionId, Section.class));
            section.setName(sectionDto.getName());
            section.setIndex(sectionDto.getIndex());
            section.setSplitRatio(sectionDto.getSplitRatio());
        }
        log.trace("saving: {}", section);
        sectionRepository.save(section);
    }

    @Transactional
    public void saveSubSection(final SubSectionDto subSectionDto) {
        log.trace("saving: {}", subSectionDto);
        final var subSectionId = subSectionDto.getId();
        final SubSection subSection;
        if (subSectionId == null) {
            subSection = pageMapper.mapSubSection(subSectionDto);
        } else {
            subSection = subSectionRepository.findById(subSectionId)
                .orElseThrow(() -> new EntityNotFoundException(subSectionId, SubSection.class));
            subSection.setName(subSectionDto.getName());
            subSection.setIndex(subSectionDto.getIndex());
            subSection.setRowTagTypeId(subSectionDto.getRowTagTypeId());
            subSection.setColumnTagTypeId(subSectionDto.getColumnTagTypeId());
        }
        log.trace("saving: {}", subSection);
        subSectionRepository.save(subSection);
    }

    @Transactional
    public void mapSubSection(final SubSectionDto subSectionDto) {
        final var subSection = subSectionRepository.findById(subSectionDto.getId())
                .orElseThrow(() -> new EntityNotFoundException(subSectionDto.getId(), SubSection.class));
        subSection.setRowTagTypeId(subSectionDto.getRowTagTypeId());
        subSection.setColumnTagTypeId(subSectionDto.getColumnTagTypeId());
        subSection.setKeys(setupMapper.mapKeyDto(subSectionDto.getKeys()));
        subSectionRepository.save(subSection);
    }

}
