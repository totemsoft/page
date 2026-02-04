package com.totemsoft.page.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.totemsoft.page.model.KeyDto;
import com.totemsoft.page.model.PageDto;
import com.totemsoft.page.model.PageResponse;
import com.totemsoft.page.model.SearchResult;
import com.totemsoft.page.model.SectionDto;
import com.totemsoft.page.model.SubSectionDto;
import com.totemsoft.page.model.TabDto;
import com.totemsoft.page.model.TagDto;
import com.totemsoft.page.service.PageService;
import com.totemsoft.page.service.PageStructureService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequiredArgsConstructor
@Log4j2
class PageStructureController {

    private final PageStructureService pageStructureService;

    private final PageService pageService;

    @PostMapping("/page")
    PageResponse savePage(@RequestBody PageDto pageDto) {
        return pageStructureService.savePage(pageDto);
    }

    @PostMapping("/page/tab")
    void saveTab(@RequestBody TabDto tabDto) {
        pageStructureService.saveTab(tabDto);
    }

    @PostMapping("/page/section")
    void saveSection(@RequestBody SectionDto sectionDto) {
        pageStructureService.saveSection(sectionDto);
    }

    @PostMapping("/page/subSection")
    void saveSubSection(@RequestBody SubSectionDto subSectionDto) {
        pageStructureService.saveSubSection(subSectionDto);
    }

    @PostMapping("/page/subSection/map")
    void mapSubSection(@RequestBody SubSectionDto subSectionDto) {
        pageStructureService.mapSubSection(subSectionDto);
    }

    @GetMapping("/page/tag/{tagTypeId}")
    SearchResult<TagDto> findTags(
            @PathVariable(name = "tagTypeId") int tagTypeId,
            @RequestParam(name = "query") String name) {
        return SearchResult.<TagDto>builder()
            .data(pageService.findTags(tagTypeId, name))
            .build();
    }

    @GetMapping("/page/key/{subSectionId}")
    SearchResult<KeyDto> findKeys(@PathVariable(name = "subSectionId") long subSectionId) {
        log.trace("#{} findKeys", subSectionId);
        return SearchResult.<KeyDto>builder()
            .data(pageService.findKeys(subSectionId))
            .build();
    }

    @PostMapping("/page/key")
    SearchResult<KeyDto> findKeys(@RequestBody Map<Integer, Object> tagTypeMap) {
        log.debug("trace: {}", tagTypeMap);
        return SearchResult.<KeyDto>builder()
            .data(pageService.findKeys(tagTypeMap))
            .build();
    }

}
