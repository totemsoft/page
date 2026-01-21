package com.totemsoft.page.model;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SubSectionDto {

    private Long id;

    @NotBlank
    private String name;

    private int index;

    private Long sectionId;

    private Integer rowTagTypeId;

    private Integer columnTagTypeId;

    private List<KeyDto> keys;

}
