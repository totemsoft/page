package com.totemsoft.page.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TagTypeDto {

    private Integer id;

    @NotBlank
    private String name;

    private String title;

}
