package com.totemsoft.page.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.totemsoft.page.model.SearchResult;
import com.totemsoft.page.model.TagDto;
import com.totemsoft.page.model.TagTypeDto;
import com.totemsoft.page.service.PageService;
import com.totemsoft.page.service.SetupService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
class SetupController {

    private final PageService pageService;

    private final SetupService setupService;

    @GetMapping("/setup/tagType")
    SearchResult<TagTypeDto> findTagTypes() {
        return SearchResult.<TagTypeDto>builder()
            .columns(TagTypeDto.columns(true))
            .data(pageService.findTagTypes())
            .build();
    }

    @PostMapping("/setup/tagType")
    void saveTagType(@RequestBody TagTypeDto tagTypeDto) {
        setupService.saveTagType(tagTypeDto);
    }

    @GetMapping("/setup/tag")
    SearchResult<TagDto> findTags(
            @RequestParam(name = "tagTypeId") Optional<Integer> tagTypeId) {
        return SearchResult.<TagDto>builder()
            .columns(TagDto.columns(true))
            .data(tagTypeId.isEmpty() ? List.of() : pageService.findTags(tagTypeId.get()))
            .build();
    }

    @PostMapping("/setup/tag")
    void saveTag(@RequestBody TagDto tagDto) {
        setupService.saveTag(tagDto);
    }

}
