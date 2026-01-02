package com.totemsoft.page.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.totemsoft.page.model.ColumnDef;
import com.totemsoft.page.model.SeriesDataDto;
import com.totemsoft.page.model.mapper.SeriesDataMapper;
import com.totemsoft.page.repository.SeriesDataRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubSectionService {

    private final SeriesDataRepository repository;

    private final SeriesDataMapper mapper;

    @Transactional()
    public List<SeriesDataDto> findData(long subSectionId) {
        return mapper.map(repository.findAll());
    }

    public List<ColumnDef> findColumns() {
        return List.of(
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
