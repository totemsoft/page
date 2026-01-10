package com.totemsoft.page.model;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class KeyDto {

    private Long id;

    @NotBlank
    private String name;

    private String title;

    private List<TagDto> tags;

}
