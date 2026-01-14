package com.totemsoft.page.model;

import java.util.List;
import java.util.stream.Collectors;

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

    public String getTagSummary() {
        return tags.stream()
            .map(TagDto::getName)
            .collect(Collectors.joining(", "));
    }

}
