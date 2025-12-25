package com.totemsoft.page.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.extern.log4j.Log4j2;

@Controller
@Log4j2
public class PageController {

    @GetMapping("/")
    public String main(
            @RequestParam(name = "pageId", required = false, defaultValue = "Data007") String pageId,
            Model model) {
        log.debug("Loading page {} ...", pageId);
        model.addAttribute("pageId", pageId);
        return "page";
    }

}
