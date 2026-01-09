package com.totemsoft.page.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.totemsoft.page.model.TabDto;
import com.totemsoft.page.service.PageStructureService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
class PageStructureController {

    private final PageStructureService pageStructureService;

    @PostMapping("/page/tab")
    void editTab(@RequestBody TabDto tabDto) {
        pageStructureService.editTab(tabDto);
    }

}
