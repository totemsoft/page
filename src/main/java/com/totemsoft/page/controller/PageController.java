package com.totemsoft.page.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.totemsoft.page.model.Page;
import com.totemsoft.page.model.Section;
import com.totemsoft.page.model.SubSection;
import com.totemsoft.page.model.Tab;
import com.totemsoft.page.model.Tag;
import com.totemsoft.page.model.refdata.SplitRatioEnum;

import lombok.extern.log4j.Log4j2;

@Controller
@Log4j2
public class PageController {

    @GetMapping("/")
    public String main(
            @RequestParam(name = "pageId", required = false, defaultValue = "0") long pageId,
            Model model) {
        log.debug("Loading page {} ...", pageId);
        model.addAttribute("page", Page.builder().id(pageId).name("Page-" + pageId)
            .tabs(List.of(
                Tab.builder().id(1).name("Tab-1")
                .sections(List.of(
                        Section.builder().id(1).name("Section-1")
                            .tag(Tag.builder().id(1).name("Tag-1").build())
                            .splitRatio(SplitRatioEnum.ONE)
                            .subSections(List.of(
                                    SubSection.builder().id(1).name("SubSection-1")
                                        .tag(Tag.builder().id(1).name("Tag-1").build())
                                        .build()
                                ))
                            .build(),
                        Section.builder().id(2).name("Section-2")
                            .tag(Tag.builder().id(2).name("Tag-2").build())
                            .build()
                    ))
                    .build(),
                Tab.builder().id(2).name("Tab-2")
                    .sections(List.of(
                        Section.builder().id(10).name("Section-10")
                            .tag(Tag.builder().id(10).name("Tag-10").build())
                            .splitRatio(SplitRatioEnum.HALF)
                            .subSections(List.of(
                                    SubSection.builder().id(10).name("SubSection-10")
                                        .tag(Tag.builder().id(10).name("Tag-10").build())
                                        .build(),
                                    SubSection.builder().id(20).name("SubSection-20")
                                        .tag(Tag.builder().id(20).name("Tag-20").build())
                                        .build()
                                ))
                            .build(),
                        Section.builder().id(20).name("Section-20")
                            .tag(Tag.builder().id(20).name("Tag-20").build())
                            .splitRatio(SplitRatioEnum.THIRD)
                            .subSections(List.of(
                                    SubSection.builder().id(200).name("SubSection-200")
                                        .tag(Tag.builder().id(200).name("Tag-200").build())
                                        .build(),
                                    SubSection.builder().id(300).name("SubSection-300")
                                        .tag(Tag.builder().id(300).name("Tag-300").build())
                                        .build(),
                                    SubSection.builder().id(400).name("SubSection-400")
                                        .tag(Tag.builder().id(400).name("Tag-400").build())
                                        .build()
                                ))
                            .build()
                    ))
                    .build()
                ))
            .build());
        return "page";
    }

}
