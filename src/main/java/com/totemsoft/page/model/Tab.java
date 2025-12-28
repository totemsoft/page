package com.totemsoft.page.model;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Tab {

    private long id;

    @NotBlank
    private String name;

    private long pageId;

    private List<Section> sections;

}
