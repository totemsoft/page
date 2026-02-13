package com.totemsoft.page.controller;

import java.time.LocalDate;
import java.util.Optional;

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

    @GetMapping("/home")
    public String home() {
        final var page = pageService.findDefaultPage();
        log.debug("Found default page {}", page.getId());
        return "redirect:/page?pageId=" + page.getId();
    }

    @GetMapping("/page")
    public String main(
            @RequestParam(name = "pageId") Long pageId,
            @RequestParam(name = "pageDate", required = false) Optional<LocalDate> pageDate,
            Model model) {
        final var date = pageDate.orElse(LocalDate.now().minusDays(1));
        log.debug("Loading page {} for date {} ...", pageId, date);
        final var page = pageService.findPage(pageId);
        log.trace("Found page {}", page.getId());
        model.addAttribute("page", page);
        model.addAttribute("pageDate", date);
        model.addAttribute("currencies", pageService.findBaseCurrencies());
        model.addAttribute("pages", pageService.findPages());
        model.addAttribute("splitRatios", SplitRatioEnum.values());
        model.addAttribute("tagTypes", pageService.findTagTypes());
        return "page";
    }

}
