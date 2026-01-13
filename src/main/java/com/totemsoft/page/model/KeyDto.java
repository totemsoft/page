package com.totemsoft.page.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(onlyExplicitlyIncluded = true)
public class KeyDto {

    @ToString.Include
    private Long id;

    @ToString.Include
    @NotBlank
    private String name;

    private String title;

    @JsonIgnore
    private List<TagDto> tags;

}
