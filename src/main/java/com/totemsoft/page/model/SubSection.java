package com.totemsoft.page.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubSection {

    private long id;

    private String name;

    private long sectionId;

    private Tag tag;

}
