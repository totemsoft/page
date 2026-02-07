package com.totemsoft.page.model;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PageDto {

    private Long id;

    @NotBlank
    private String name;

    private List<TabDto> tabs;

}
