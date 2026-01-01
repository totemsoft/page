package com.totemsoft.page.controller;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.totemsoft.page.model.ColumnDef;
import com.totemsoft.page.model.SeriesData;
import com.totemsoft.page.model.SeriesDataResult;

import lombok.extern.log4j.Log4j2;

@RestController
@Log4j2
class SubSectionController {

    @GetMapping("/subSection/{subSectionId}")
    SeriesDataResult subSectionData(@PathVariable long subSectionId) {
        log.debug("Loading data for subSection {} ...", subSectionId);
        return SeriesDataResult.builder()
            .records(data())
            .columns(columns())
            .build();
    }

    @GetMapping("/subSection/{subSectionId}/columns")
    SeriesDataResult subSectionColumns(@PathVariable long subSectionId) {
        log.debug("Loading column definitions for subSection {} ...", subSectionId);
        return SeriesDataResult.builder()
            .columns(columns())
            .build();
    }

    private List<SeriesData> data() {
        return List.of(
            SeriesData.builder()
            .id(1)
            .date(new Date())
            .value(new BigDecimal("1234.5678"))
            .title("Read Me Twice")
            .build()
        );
    }

    private List<ColumnDef> columns() {
        return List.of(
            ColumnDef.builder()
                .key("id")
                .label("ID")
                .build(),
            ColumnDef.builder()
                .key("date")
                .label("Date")
                .formatter("YAHOO.widget.DataTable.formatDate")
                .build(),
            ColumnDef.builder()
                .key("value")
                .label("Value")
                .build(),
            ColumnDef.builder()
                .key("title")
                .label("Name")
                .build()
            );
    }

}
