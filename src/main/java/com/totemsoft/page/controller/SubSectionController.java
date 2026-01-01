package com.totemsoft.page.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.totemsoft.page.model.SeriesDataResult;
import com.totemsoft.page.service.SubSectionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequiredArgsConstructor
@Log4j2
class SubSectionController {

    private final SubSectionService subSectionService;

    @GetMapping("/subSection/{subSectionId}")
    SeriesDataResult subSectionData(@PathVariable long subSectionId) {
        log.debug("Loading data for subSection {} ...", subSectionId);
        return SeriesDataResult.builder()
            .records(subSectionService.findData(subSectionId))
            .columns(subSectionService.findColumns())
            .build();
    }

    @GetMapping("/subSection/{subSectionId}/columns")
    SeriesDataResult subSectionColumns(@PathVariable long subSectionId) {
        log.debug("Loading column definitions for subSection {} ...", subSectionId);
        return SeriesDataResult.builder()
            .columns(subSectionService.findColumns())
            .build();
    }

}
