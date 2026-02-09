package com.totemsoft.page.controller;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.totemsoft.page.model.Row;
import com.totemsoft.page.model.SearchResult;
import com.totemsoft.page.service.SubSectionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
class SubSectionController {

    private final SubSectionService subSectionService;

    @GetMapping("/subSection/{subSectionId}")
    SearchResult<Row> subSectionData(
            @PathVariable long subSectionId,
            @RequestParam(name = "rowTagTypeId") Optional<Integer> rowTagTypeId,
            @RequestParam(name = "columnTagTypeId") Optional<Integer> columnTagTypeId) {
        return subSectionService.find(subSectionId, rowTagTypeId, columnTagTypeId);
    }

    @GetMapping("/subSection/{subSectionId}/{date}")
    SearchResult<?> subSectionData(
            @PathVariable long subSectionId,
            @PathVariable LocalDate date,
            @RequestParam(name = "skipColumns") Optional<Boolean> skipColumns) {
        return subSectionService.find(subSectionId, date, skipColumns);
    }

}
