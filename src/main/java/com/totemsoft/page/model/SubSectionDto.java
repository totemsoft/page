package com.totemsoft.page.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SubSectionDto {

    private long id;

    @NotBlank
    private String name;

    private long sectionId;

}
