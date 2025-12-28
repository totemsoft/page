package com.totemsoft.page.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Tag {

    private long id;

    @NotBlank
    private String name;

}
