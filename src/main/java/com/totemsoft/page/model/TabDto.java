package com.totemsoft.page.model;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TabDto {

    private Long id;

    @NotBlank
    private String name;

    private long pageId;

    private List<SectionDto> sections;

}
