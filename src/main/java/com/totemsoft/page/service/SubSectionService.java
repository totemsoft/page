package com.totemsoft.page.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.totemsoft.page.model.ColumnDef;
import com.totemsoft.page.model.SeriesDataDto;
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
        final var subSection =repository.findById(subSectionId)
            .orElseThrow(() -> new EntityNotFoundException(subSectionId, SubSection.class));
        final var keys = subSection.getKeys();
        if (keys.isEmpty()) {
            return List.of();
        }
        final var data = seriesDataRepository.findByKeyIn(keys);
        //log.debug("findData({}): {}", subSectionId, data);
        return mapper.map(data);
    }

    public List<ColumnDef> findColumns() {
        return List.of(
            ColumnDef.builder()
                .key("tag")
                .label("")
                .formatter("YAHOO.widget.DataTable.formatTag")
                .build(),
            ColumnDef.builder()
                .key("id")
                .label("ID")
                //.hidden(true) // TODO: fix
                .formatter("YAHOO.widget.DataTable.formatNumber")
                .build(),
            ColumnDef.builder()
                .key("date")
                .label("Date")
                //.formatter("YAHOO.widget.DataTable.formatDate")
                //.dateOptions("{format: '%d/%m/%Y', locale: 'en'}")
                .build(),
            ColumnDef.builder()
                .key("value")
                .label("Value")
                .formatter("YAHOO.widget.DataTable.formatCurrency")
                //.currencyOptions("{}")
                .build(),
            ColumnDef.builder()
                .key("title")
                .label("Name")
                .build()
            );
    }

}
