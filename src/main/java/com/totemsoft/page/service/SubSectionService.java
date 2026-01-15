package com.totemsoft.page.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.stereotype.Service;

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
@RequiredArgsConstructor
@Log4j2
public class SubSectionService {

    private final SubSectionRepository subSectionRepository;

    private final SeriesDataRepository seriesDataRepository;

    private final SeriesDataMapper mapper;

    @Transactional
    public SubSectionResult<?> find(long subSectionId, LocalDate date) {
        log.trace("findRows({}, {}) ...", subSectionId, date);
        final var subSection = subSectionRepository.findById(subSectionId)
            .orElseThrow(() -> new EntityNotFoundException(subSectionId, SubSection.class));
        final var data = findSeriesData(subSection, date);
        //
        final var rowTagType = subSection.getRowTagType();
        final var columnTagType = subSection.getColumnTagType();
        if (rowTagType == null || columnTagType == null) {
            log.trace("No RowTagType/ColumnTagType set for sub-section {}.", subSectionId);
            return SubSectionResult.<SeriesDataDto>builder()
                    .columns(findDefaultColumns())
                    .data(mapper.map(data))
                    .build();
        }
        // all keys from sub-section
        final var keys = subSection.getKeys();
        // unique sub-section row/column tags
        final var rowTags = new TreeSet<Tag>();
        final var columnTags = new TreeSet<Tag>();
        keys.forEach(key -> {
            key.findTag(rowTagType).ifPresent(rowTags::add);
            key.findTag(columnTagType).ifPresent(columnTags::add);
        });
        log.trace("#{} rowTags: {}", subSectionId, rowTags);
        log.trace("#{} columnTags: {}", subSectionId, columnTags);
        //
        final var result = new ArrayList<Row>();
        //final var dataTags = data.stream().flatMap(d -> d.getKey().getTags().stream()).toList();
        rowTags.forEach(rowTag -> result.add(Row.builder()
            .cells(cells(data.stream().filter(d -> d.getKey().anyMatch(rowTag)).toList(), rowTag, columnTags))
            .build())
        );
        return SubSectionResult.<Row>builder()
            .columns(findColumns(columnTags))
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

    private Map<String, Cell<?>> cells(List<SeriesData> data, Tag rowTag, Set<Tag> columnTags) {
        final var cells = new HashMap<String, Cell<?>>(1 + columnTags.size());
        cells.put("TAG", Cell.<String>builder()
            .id(rowTag.getId())
            .value(rowTag.getTitle())
            .build());
        columnTags.forEach(columnTag -> data.stream()
            .filter(d -> d.getKey().anyMatch(columnTag))
            .forEach(d -> cells.put(columnTag.getName(), mapper.map(d)))
        );
        return cells;
    }

    private List<ColumnDef> findColumns(Set<Tag> columnTags) {
        final var columnDefs = new ArrayList<ColumnDef>();
        columnDefs.add(ColumnDef.builder()
            .key("TAG")
            .label("")
            .formatter(FORMATTER.TAG.name().toLowerCase())
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
