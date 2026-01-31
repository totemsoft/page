package com.totemsoft.page.service;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.totemsoft.page.config.SecurityConfig;
import com.totemsoft.page.model.TagDto;
import com.totemsoft.page.model.TagTypeDto;
import com.totemsoft.page.model.entity.Tag;
import com.totemsoft.page.model.entity.TagType;
import com.totemsoft.page.model.mapper.PageMapper;
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

    private final PageMapper pageMapper;

    private final TagRepository tagRepository;

    private final TagTypeRepository tagTypeRepository;

    @Transactional
    public void saveTag(TagDto dto) {
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
    }

    @Transactional
    public void saveTagType(TagTypeDto dto) {
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
    }

}
