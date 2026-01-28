package com.totemsoft.page.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.totemsoft.page.config.SecurityConfig;
import com.totemsoft.page.model.Cell;
import com.totemsoft.page.model.ColumnDef;
import com.totemsoft.page.model.ColumnDef.FORMATTER;
import com.totemsoft.page.model.CssClasName;
import com.totemsoft.page.model.Row;
import com.totemsoft.page.model.SeriesDataDto;
import com.totemsoft.page.model.SubSectionResult;
import com.totemsoft.page.model.entity.SeriesData;
import com.totemsoft.page.model.entity.SubSection;
import com.totemsoft.page.model.entity.Tag;
import com.totemsoft.page.model.mapper.SeriesDataMapper;
import com.totemsoft.page.repository.SeriesDataRepository;
import com.totemsoft.page.repository.SubSectionRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@PreAuthorize(SecurityConfig.IS_AUTHENTICATED)
@RequiredArgsConstructor
@Log4j2
public class SubSectionService {

    private final SubSectionRepository subSectionRepository;

    private final SeriesDataRepository seriesDataRepository;

    private final SeriesDataMapper mapper;

    @Transactional
    public SubSectionResult<Row> find(long subSectionId,
            Optional<Integer> rowTagTypeId,
            Optional<Integer> columnTagTypeId) {
        log.debug("find({}, {}, {}) ...", subSectionId, rowTagTypeId, columnTagTypeId);
        final var subSection = subSectionRepository.findById(subSectionId)
            .orElseThrow(() -> new EntityNotFoundException(subSectionId, SubSection.class));
        //
        if (rowTagTypeId.isEmpty() || columnTagTypeId.isEmpty()) {
            log.trace("No RowTagType/ColumnTagType set for sub-section {}.", subSectionId);
            return SubSectionResult.<Row>builder()
                .columns(findColumns(Set.of()))
                .data(List.of())
                .build();
        }
        // all keys from sub-section
        final var keys = subSection.getKeys();
        // unique sub-section row/column tags
        final var rowTags = new TreeSet<Tag>();
        final var columnTags = new TreeSet<Tag>();
        keys.forEach(key -> {
            key.findTag(rowTagTypeId.get()).ifPresent(rowTags::add);
            key.findTag(columnTagTypeId.get()).ifPresent(columnTags::add);
        });
        log.trace("#{} rowTags: {}", subSectionId, rowTags);
        log.trace("#{} columnTags: {}", subSectionId, columnTags);
        final var result = new ArrayList<Row>();
        rowTags.forEach(rowTag -> result.add(Row.builder()
            .cells(filterRowCells(List.of(), rowTag, columnTags))
            .build())
        );
        return SubSectionResult.<Row>builder()
            .columns(findColumns(columnTags))
            .data(result)
            .build();
    }

    @Transactional
    public SubSectionResult<?> find(long subSectionId, LocalDate date, Optional<Boolean> skipColumns) {
        log.trace("findRows({}, {}) ...", subSectionId, date);
        final var subSection = subSectionRepository.findById(subSectionId)
            .orElseThrow(() -> new EntityNotFoundException(subSectionId, SubSection.class));
        final var data = findSeriesData(subSection, date);
        //
        final var rowTagTypeId = subSection.getRowTagTypeId();
        final var columnTagTypeId = subSection.getColumnTagTypeId();
        if (rowTagTypeId == null || columnTagTypeId == null) {
            log.trace("No RowTagType/ColumnTagType set for sub-section {}.", subSectionId);
            return SubSectionResult.<SeriesDataDto>builder()
                .columns(skipColumns.orElse(false) ? null : findDefaultColumns())
                .data(mapper.map(data))
                .build();
        }
        // all keys from sub-section
        final var keys = subSection.getKeys();
        // unique sub-section row/column tags
        final var rowTags = new TreeSet<Tag>();
        final var columnTags = new TreeSet<Tag>();
        keys.forEach(key -> {
            key.findTag(rowTagTypeId).ifPresent(rowTags::add);
            key.findTag(columnTagTypeId).ifPresent(columnTags::add);
        });
        log.trace("#{} rowTags: {}", subSectionId, rowTags);
        log.trace("#{} columnTags: {}", subSectionId, columnTags);
        final var result = new ArrayList<Row>();
        rowTags.forEach(rowTag -> result.add(Row.builder()
            .cells(filterRowCells(
                data.stream().filter(d -> d.getKey().anyMatch(rowTag.getId())).toList(),
                rowTag, columnTags))
            .build())
        );
        return SubSectionResult.<Row>builder()
            .columns(skipColumns.orElse(false) ? null : findColumns(columnTags))
            .data(result)
            .build();
    }

    private List<SeriesData> findSeriesData(SubSection subSection, LocalDate date) {
        // all keys from sub-section
        final var keys = subSection.getKeys();
        if (keys.isEmpty()) {
            log.warn("No key(s) found for sub-section {}.", subSection.getId());
            return List.of();
        }
        return seriesDataRepository.findByDateIsAndKeyIn(date, keys);
    }

    private Map<String, Cell<?>> filterRowCells(List<SeriesData> data, Tag rowTag, Set<Tag> columnTags) {
        final var cells = new HashMap<String, Cell<?>>(1 + columnTags.size());
        cells.put(ColumnDef.COLUMN_TAG, Cell.<String>builder()
            .id(rowTag.getId())
            .value(rowTag.getTitle())
            .build());
        columnTags.forEach(columnTag -> data.stream()
            .filter(d -> d.getKey().anyMatch(columnTag.getId()))
            .forEach(d -> cells.put(columnTag.getName(), mapper.map(d)))
        );
        return cells;
    }

    private List<ColumnDef> findColumns(Set<Tag> columnTags) {
        final var columnDefs = new ArrayList<ColumnDef>();
        columnDefs.add(ColumnDef.builder()
            .key(ColumnDef.COLUMN_TAG)
            .label("&#160;") // &nbsp;
            .formatter(ColumnDef.COLUMN_TAG.toLowerCase())
            .className(CssClasName.TAG)
            .build());
        columnTags.forEach(t -> columnDefs.add(ColumnDef.builder()
            .key(t.getName())
            .label(t.getTitle())
            .formatter(FORMATTER.CURRENCY.name().toLowerCase())
            .className(CssClasName.RIGHT)
            .build()));
        return columnDefs;
    }

    private List<ColumnDef> findDefaultColumns() {
        return List.of(
            ColumnDef.builder()
                .key("id")
                .label("ID")
                //.hidden(true) // TODO: fix dataTable.doBeforeLoadData insertColumn issue
                .formatter(FORMATTER.NUMBER.name().toLowerCase())
                .className(CssClasName.RIGHT)
                .build(),
            ColumnDef.builder()
                .key("date")
                .label("Date")
                //.formatter(FORMATTER.DATE.name().toLowerCase())
                //.dateOptions("{format: '%d/%m/%Y', locale: 'en'}")
                .build(),
            ColumnDef.builder()
                .key("value")
                .label("Value")
                .formatter(FORMATTER.CURRENCY.name().toLowerCase())
                //.currencyOptions("{}")
                .className(CssClasName.RIGHT)
                .build(),
            ColumnDef.builder()
                .key("title")
                .label("Name")
                .build()
            );
    }

}
