package com.totemsoft.page.model;

import java.util.List;

import com.totemsoft.page.model.refdata.SplitRatioEnum;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class SectionDto {

    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    @NotBlank
    private String name;

    private int index;

    @NotNull
    private SplitRatioEnum splitRatio = SplitRatioEnum.ONE;

    private Long tabId;

    private List<SubSectionDto> subSections;

}
