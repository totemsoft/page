package com.totemsoft.page.model;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Page {

    private long id;

    private String name;

    private List<Tab> tabs;

}
