package com.totemsoft.page.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.totemsoft.page.model.SearchResult;
import com.totemsoft.page.model.TagDto;
import com.totemsoft.page.model.TagTypeDto;
import com.totemsoft.page.service.PageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
class SetupController {

    private final PageService pageService;

    @GetMapping("/setup/tagTypes")
    SearchResult<TagTypeDto> findTagTypes() {
        return SearchResult.<TagTypeDto>builder()
            .columns(TagTypeDto.columns())
            .data(pageService.findTagTypes())
            .build();
    }

    @GetMapping("/setup/tags")
    SearchResult<TagDto> findTags(
            @RequestParam(name = "tagTypeId") Optional<Integer> tagTypeId) {
        return SearchResult.<TagDto>builder()
            .columns(TagDto.columns())
            .data(tagTypeId.isEmpty() ? List.of() : pageService.findTags(tagTypeId.get()))
            .build();
    }

}
