package com.totemsoft.page.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchResult<T> {

    private List<ColumnDef> columns;

    @JsonIgnore
    private SearchData<T> data;

    public List<T> getRecords() {
        return data.getRecords();
    }

    public Integer getLimit() {
        return data.getLimit();
    }

    public Integer getOffset() {
        return data.getOffset();
    }

    public Integer getTotal() {
        return data.getTotal();
    }

    public String getSort() {
        return data.getSort();
    }

    public String getDir() {
        return data.getDir();
    }

}
