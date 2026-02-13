package com.totemsoft.page.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.totemsoft.page.config.SecurityConfig;
import com.totemsoft.page.model.Cell;
import com.totemsoft.page.model.ColumnDef;
import com.totemsoft.page.model.Row;
import com.totemsoft.page.model.SearchResult;
import com.totemsoft.page.model.SeriesDataDto;
import com.totemsoft.page.model.ColumnDef.CellFormatterEnum;
import com.totemsoft.page.model.entity.ExchangeRate;
import com.totemsoft.page.model.entity.ExchangeRateId;
import com.totemsoft.page.model.entity.SeriesData;
import com.totemsoft.page.model.entity.SubSection;
import com.totemsoft.page.model.entity.Tag;
import com.totemsoft.page.model.mapper.SeriesDataMapper;
import com.totemsoft.page.repository.ExchangeRateRepository;
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

    /** exchangeratesapi base currency */
    @Value("${page.exchangeratesapi.io.base-currency}")
    private String baseCurrency;

    private final ExchangeRateRepository exchangeRateRepository;

    private final SubSectionRepository subSectionRepository;

    private final SeriesDataRepository seriesDataRepository;

    private final SeriesDataMapper seriesDataMapper;

    @Transactional
    public SearchResult<Row> find(long subSectionId,
            Optional<Integer> rowTagTypeId,
            Optional<Integer> columnTagTypeId) {
        log.debug("find({}, {}, {}) ...", subSectionId, rowTagTypeId, columnTagTypeId);
        final var subSection = subSectionRepository.findById(subSectionId)
            .orElseThrow(() -> new EntityNotFoundException(subSectionId, SubSection.class));
        //
        if (rowTagTypeId.isEmpty() || columnTagTypeId.isEmpty()) {
            log.trace("No RowTagType/ColumnTagType set for sub-section {}.", subSectionId);
            return SearchResult.<Row>builder()
                .columns(Row.columns(Set.of(), Optional.empty()))
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
            .cells(filterRowCells(
                List.of(),
                rowTag,
                columnTags,
                Optional.empty()))
            .build())
        );
        return SearchResult.<Row>builder()
            .columns(Row.columns(columnTags, Optional.empty()))
            .data(result)
            .build();
    }

    @Transactional
    public SearchResult<?> find(
            long subSectionId,
            LocalDate date,
            String currency,
            Optional<Boolean> skipColumns) {
        log.trace("findRows({}, {}, {}, {}) ...", subSectionId, date, currency, skipColumns);
        final var subSection = subSectionRepository.findById(subSectionId)
            .orElseThrow(() -> new EntityNotFoundException(subSectionId, SubSection.class));
        // in baseCurrency
        final var data = findSeriesData(subSection, date);
        final Optional<ExchangeRate> exchangeRate;
        // will be used to convert data value from base to other currency
        if (!baseCurrency.equals(currency)) {
            final var exchangeRateId = ExchangeRateId.builder()
                .date(date)
                .base(baseCurrency)
                .code(currency)
                .build();
            exchangeRate = exchangeRateRepository.findById(exchangeRateId);
        } else {
            exchangeRate = Optional.empty();
        }
        //
        final var rowTagTypeId = subSection.getRowTagTypeId();
        final var columnTagTypeId = subSection.getColumnTagTypeId();
        if (rowTagTypeId == null || columnTagTypeId == null) {
            log.trace("No RowTagType/ColumnTagType set for sub-section {}.", subSectionId);
            return SearchResult.<SeriesDataDto>builder()
                .columns(skipColumns.orElse(false) ? null
                    : SeriesDataDto.columns())
                .data(seriesDataMapper.map(data, exchangeRate))
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
                rowTag,
                columnTags,
                exchangeRate))
            .build())
        );
        final var sameCurrency = data.stream().allMatch(SeriesData::sameCurrency);
        final Optional<CellFormatterEnum> formatter = sameCurrency ? Optional.of(CellFormatterEnum.CURRENCY) : Optional.empty();
        return SearchResult.<Row>builder()
            .columns(skipColumns.orElse(false) ? null
                : Row.columns(columnTags, formatter))
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
        return seriesDataRepository.findByDateAndKeyIn(date, keys);
    }

    private Map<String, Cell<?>> filterRowCells(
            List<SeriesData> data,
            Tag rowTag,
            Set<Tag> columnTags,
            Optional<ExchangeRate> exchangeRate) {
        final var cells = new HashMap<String, Cell<?>>(1 + columnTags.size());
        cells.put(ColumnDef.COLUMN_TAG, Cell.<String>builder()
            .id(rowTag.getId())
            .value(rowTag.getLabel())
            .build());
        columnTags.forEach(columnTag -> data.stream()
            .filter(d -> d.getKey().anyMatch(columnTag.getId()))
            .forEach(d -> cells.put(columnTag.getKey(), seriesDataMapper.map(d, exchangeRate)))
        );
        return cells;
    }

}
