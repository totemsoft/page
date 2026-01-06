package com.totemsoft.page.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.totemsoft.page.model.ColumnDef;
import com.totemsoft.page.model.SeriesDataDto;
import com.totemsoft.page.model.entity.Key;
import com.totemsoft.page.model.entity.SubSection;
import com.totemsoft.page.model.mapper.SeriesDataMapper;
import com.totemsoft.page.repository.SeriesDataRepository;
import com.totemsoft.page.repository.SubSectionRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class SubSectionService {

    private final SubSectionRepository repository;

    private final SeriesDataRepository seriesDataRepository;

    private final SeriesDataMapper mapper;

    @Transactional
    public List<SeriesDataDto> findData(long subSectionId) {
        log.trace("findData({}) ...", subSectionId);
        final var keys = findKeys(subSectionId);
        if (keys.isEmpty()) {
            return List.of();
        }
        final var data = seriesDataRepository.findByKeyIn(keys);
        return mapper.map(data);
    }

    @Transactional
    public List<ColumnDef> findColumns(long subSectionId) {
        log.trace("findColumns({}) ...", subSectionId);
        //final var keys = findKeys(subSectionId);
        //keys.stream().
        return List.of(
            ColumnDef.builder()
                .key("tag")
                .label("")
                .formatter("tag")
                .build(),
            ColumnDef.builder()
                .key("id")
                .label("ID")
                //.hidden(true) // TODO: fix
                .formatter("number")
                .className("right")
                .build(),
            ColumnDef.builder()
                .key("date")
                .label("Date")
                //.formatter("date")
                //.dateOptions("{format: '%d/%m/%Y', locale: 'en'}")
                .build(),
            ColumnDef.builder()
                .key("value")
                .label("Value")
                .formatter("currency")
                //.currencyOptions("{}")
                .className("right")
                .build(),
            ColumnDef.builder()
                .key("title")
                .label("Name")
                .build()
            );
    }

    private List<Key> findKeys(long subSectionId) {
        final var subSection = repository.findById(subSectionId)
            .orElseThrow(() -> new EntityNotFoundException(subSectionId, SubSection.class));
        return subSection.getKeys();
    }

}
