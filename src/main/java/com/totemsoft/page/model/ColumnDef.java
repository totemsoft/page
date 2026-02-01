package com.totemsoft.page.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

/**
 * @see https://yui.github.io/yui2/docs/yui_2.9.0/docs/YAHOO.widget.Column.html
    key: 'date'
    label: 'Date'
    formatter: 'date'
        currencyOptions: {}
        dateOptions: {format: '%d/%m/%Y', locale: 'en'}
        dropdownOptions:
    resizeable: true
    sortable: true
    sortOptions: {defaultDir:YAHOO.widget.DataTable.CLASS_DESC}
 */
@Data
@Builder
public class ColumnDef {

    public enum CellFormatterEnum {
        CURRENCY,
        DATE,
        NUMBER,
        TAG;
    }

    public static final String COLUMN_TAG = CellFormatterEnum.TAG.name();

    public enum CellEditorEnum {
        CHECKBOX,
        DATE,
        DROPDOWN,
        RADIO,
        TEXTAREA,
        TEXTBOX;
    }

    @NotBlank
    private String key;

    private String label;

    private String formatter;

    private String currencyOptions;

    private String dateOptions;

    private String editor;

    private boolean hidden;

    private boolean resizeable;

    private boolean sortable;

    private String sortOptions;

    private String className;

    private Integer width;

    private Integer minWidth;

    private Integer maxAutoWidth;

}
