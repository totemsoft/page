package com.totemsoft.page.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.totemsoft.page.model.KeyDto;
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
    Integer saveTagType(@RequestBody TagTypeDto tagTypeDto) {
        return setupService.saveTagType(tagTypeDto);
    }

    @GetMapping("/setup/tag")
    SearchResult<TagDto> findTags(
            @RequestParam(name = "tagTypeId") Optional<Integer> tagTypeId) {
        return SearchResult.<TagDto>builder()
            .columns(TagDto.columns(true))
            .data(tagTypeId.isEmpty() ? List.of() : pageService.findTags(tagTypeId.get()))
            .build();
    }

    @GetMapping("/setup/tagByKey/{keyId}")
    SearchResult<TagDto> findTagsByKey(@PathVariable long keyId) {
        return SearchResult.<TagDto>builder()
            .columns(TagDto.columns(false))
            .data(setupService.findTagsByKey(keyId))
            .build();
    }

    @PostMapping("/setup/tag")
    Long saveTag(@RequestBody TagDto tagDto) {
        return setupService.saveTag(tagDto);
    }

    @GetMapping("/setup/key")
    SearchResult<KeyDto> findKeys() {
        return SearchResult.<KeyDto>builder()
            .columns(KeyDto.columns(true))
            .data(setupService.findKeys())
            .build();
    }

    @PostMapping("/setup/key")
    Long saveKey(@RequestBody KeyDto keyDto) {
        return setupService.saveKey(keyDto);
    }

}
