package com.totemsoft.page.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubSection {

    private long id;

    @NotBlank
    private String name;

    private long sectionId;

    private Tag tag;

}
