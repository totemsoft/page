package com.totemsoft.page.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchData<T> {

    @JsonProperty("records")
    private List<T> records;

    private Integer page;

    private Integer offset;

    private Integer limit;

    private Integer total;

    private String sort;

    private String dir;

}
