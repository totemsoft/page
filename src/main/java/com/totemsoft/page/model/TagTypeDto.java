package com.totemsoft.page.model;

import java.util.List;

import com.totemsoft.page.model.ColumnDef.CellEditorEnum;
import com.totemsoft.page.model.ColumnDef.CellFormatterEnum;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TagTypeDto {

    private Integer id;

    @NotBlank
    private String name;

    private String title;

    public static List<ColumnDef> columns(boolean editable) {
        return List.of(
            ColumnDef.builder()
                .key("id")
                .label("ID")
                //.hidden(true) // TODO: fix dataTable.doBeforeLoadData insertColumn issue
                .formatter(CellFormatterEnum.NUMBER.name().toLowerCase())
                .className(CssClasName.RIGHT)
                .width(50)
                .build(),
            ColumnDef.builder()
                .key("name")
                .label("Name")
                .sortable(true)
                .editor(editable ? CellEditorEnum.TEXTBOX.name().toLowerCase() : null)
                .build(),
            ColumnDef.builder()
                .key("title")
                .label("Title")
                .editor(editable ? CellEditorEnum.TEXTAREA.name().toLowerCase() : null)
                .build()
            );
    }

}
