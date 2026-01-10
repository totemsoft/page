package com.totemsoft.page.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.totemsoft.page.model.PageDto;
import com.totemsoft.page.model.PageResponse;
import com.totemsoft.page.model.SectionDto;
import com.totemsoft.page.model.SubSectionDto;
import com.totemsoft.page.model.TabDto;
import com.totemsoft.page.service.PageStructureService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
class PageStructureController {

    private final PageStructureService pageStructureService;

    @PostMapping("/page")
    PageResponse editPage(@RequestBody PageDto pageDto) {
        return pageStructureService.editPage(pageDto);
    }

    @PostMapping("/page/tab")
    void editTab(@RequestBody TabDto tabDto) {
        pageStructureService.editTab(tabDto);
    }

    @PostMapping("/page/section")
    void editSection(@RequestBody SectionDto sectionDto) {
        pageStructureService.editSection(sectionDto);
    }

    @PostMapping("/page/subSection")
    void editSubSection(@RequestBody SubSectionDto subSectionDto) {
        pageStructureService.editSubSection(subSectionDto);
    }

}
