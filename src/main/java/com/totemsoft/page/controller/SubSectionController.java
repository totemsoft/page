package com.totemsoft.page.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.totemsoft.page.model.SubSectionResult;
import com.totemsoft.page.service.SubSectionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
class SubSectionController {

    private final SubSectionService subSectionService;

    @GetMapping("/subSection/{subSectionId}")
    SubSectionResult<?> subSectionData(@PathVariable long subSectionId) {
        return subSectionService.find(subSectionId);
    }

}
