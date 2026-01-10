package com.totemsoft.page.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SubSectionDto {

    private Long id;

    @NotBlank
    private String name;

    private long sectionId;

}
