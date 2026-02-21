package com.totemsoft.page.exchangerates.v1.model;

import java.io.Serializable;
import java.util.List;

import com.totemsoft.page.model.ColumnDef;
import com.totemsoft.page.model.ColumnDef.CellFormatterEnum;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class CurrencyDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @EqualsAndHashCode.Include
    @ToString.Include
    private String code;

    private String title;

    private Boolean base;

    public static List<ColumnDef> columns(boolean editable) {
        return List.of(
            ColumnDef.builder()
                .key("code")
                .label("Code")
                .width(100)
                .build(),
            ColumnDef.builder()
                .key("title")
                .label("Title")
                .build(),
            ColumnDef.builder()
                .key("base")
                .label("Base")
                .formatter(editable ? CellFormatterEnum.CHECKBOX.name().toLowerCase() : null)
                .build()
            );
    }

}
