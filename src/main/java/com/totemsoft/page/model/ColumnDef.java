package com.totemsoft.page.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

/**
 * @see https://yui.github.io/yui2/docs/yui_2.9.0/docs/YAHOO.widget.Column.html
    key: 'date'
    label: 'Date'
    formatter: YAHOO.widget.DataTable.formatDate
    resizeable: true
    sortable: true
    sortOptions: {defaultDir:YAHOO.widget.DataTable.CLASS_DESC}
 */
@Data
@Builder
@JsonInclude(Include.NON_NULL)
public class ColumnDef {

    @NotBlank
    private String key;

    private String label;

    private String formatter;

    private boolean resizeable;

    private boolean sortable;

    private String sortOptions;

}
