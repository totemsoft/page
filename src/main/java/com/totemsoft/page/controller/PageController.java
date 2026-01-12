package com.totemsoft.page.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.totemsoft.page.model.refdata.SplitRatioEnum;
import com.totemsoft.page.service.PageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Controller
@RequiredArgsConstructor
@Log4j2
class PageController {

    private final PageService pageService;

    @GetMapping("/page")
    public String main(
            @RequestParam(name = "pageId") long pageId,
            Model model) {
        log.debug("Loading page {} ...", pageId);
        final var page = pageService.findPage(pageId);
        log.trace("Found page {}.", page);
        model.addAttribute("page", page);
        model.addAttribute("splitRatios", SplitRatioEnum.values());
        model.addAttribute("tagTypes", pageService.findTagTypes());
        return "page";
    }

}
