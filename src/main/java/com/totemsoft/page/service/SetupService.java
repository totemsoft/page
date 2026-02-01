package com.totemsoft.page.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.totemsoft.page.config.SecurityConfig;
import com.totemsoft.page.model.KeyDto;
import com.totemsoft.page.model.TagDto;
import com.totemsoft.page.model.TagTypeDto;
import com.totemsoft.page.model.entity.Key;
import com.totemsoft.page.model.entity.Tag;
import com.totemsoft.page.model.entity.TagType;
import com.totemsoft.page.model.mapper.PageMapper;
import com.totemsoft.page.model.mapper.SetupMapper;
import com.totemsoft.page.repository.KeyRepository;
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

    private final SetupMapper setupMapper;

    private final KeyRepository keyRepository;

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

}
