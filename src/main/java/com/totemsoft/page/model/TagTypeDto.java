package com.totemsoft.page.model;

import java.util.List;

import com.totemsoft.page.model.ColumnDef.FORMATTER;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TagTypeDto {

    private Integer id;

    @NotBlank
    private String name;

    private String title;

    public static List<ColumnDef> columns() {
        return List.of(
            ColumnDef.builder()
                .key("id")
                .label("ID")
                //.hidden(true) // TODO: fix dataTable.doBeforeLoadData insertColumn issue
                .formatter(FORMATTER.NUMBER.name().toLowerCase())
                .className(CssClasName.RIGHT)
                .build(),
            ColumnDef.builder()
                .key("name")
                .label("Name")
                .build(),
            ColumnDef.builder()
                .key("title")
                .label("Title")
                .build()
            );
    }

}
