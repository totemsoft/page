package com.totemsoft.page.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TagDto {

    private long id;

    @NotBlank
    private String name;

    private String title;

    private TagTypeDto tagTypeDto;

}
