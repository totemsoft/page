package com.totemsoft.page.model;

import java.util.List;

import com.totemsoft.page.model.refdata.SplitRatioEnum;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SectionDto {

    private Long id;

    @NotBlank
    private String name;

    /**
     * vertical position (row index within the tab: 0..n)
     */
    private int index;

    @NotNull
    private SplitRatioEnum splitRatio = SplitRatioEnum.ONE;

    private Long tabId;

    private List<SubSectionDto> subSections;

}
