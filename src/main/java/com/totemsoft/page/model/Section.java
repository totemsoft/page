package com.totemsoft.page.model;

import java.util.List;

import com.totemsoft.page.model.refdata.SplitRatioEnum;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Section {

    private long id;

    @NotBlank
    private String name;

    private long tabId;

    /**
     * vertical position (row index within the tab: 0..n) (ORDER BY)
     */
    private int index;

    private Tag tag;

    @NotNull
    @Builder.Default
    private SplitRatioEnum splitRatio = SplitRatioEnum.NONE;

    @Max(value = 3)
    private List<SubSection> subSections;

}
