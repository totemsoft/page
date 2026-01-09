package com.totemsoft.page.service;

import org.springframework.stereotype.Service;

import com.totemsoft.page.model.TabDto;
import com.totemsoft.page.model.entity.Tab;
import com.totemsoft.page.model.mapper.PageMapper;
import com.totemsoft.page.repository.TabRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class PageStructureService {

    private final PageMapper pageMapper;

    private final TabRepository tabRepository;

    @Transactional
    public void editTab(TabDto tabDto) {
        log.debug("saving: {}", tabDto);
        final var tabId = tabDto.getId();
        final Tab tab;
        if (tabId == null) {
            tab = pageMapper.map(tabDto);
        } else {
            tab = tabRepository.findById(tabId)
                .orElseThrow(() -> new EntityNotFoundException(tabId, Tab.class));
            tab.setName(tabDto.getName());
        }
        log.debug("saving: {}", tab);
        tabRepository.save(tab);
    }

}
