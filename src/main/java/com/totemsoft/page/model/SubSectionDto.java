package com.totemsoft.page.model;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class SubSectionDto {

    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    @NotBlank
    private String name;

    private int index;

    private Long sectionId;

    private Integer rowTagTypeId;

    private Integer columnTagTypeId;

    private List<KeyDto> keys;

}
