package com.totemsoft.page.model;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Section {

    private long id;

    private String name;

    private long tabId;

    /**
     * vertical position (row index within the tab: 0..n)
     */
    private int index;

    private Tag tag;

    private List<SubSection> subSections;

}
