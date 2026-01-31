package com.totemsoft.page.model;

import java.util.List;

import com.totemsoft.page.model.ColumnDef.CellEditorEnum;
import com.totemsoft.page.model.ColumnDef.FormatterEnum;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TagDto {

    private Long id;

    @NotBlank
    private String name;

    private String title;

    private Integer tagTypeId;

    public static List<ColumnDef> columns(boolean editable) {
        return List.of(
            ColumnDef.builder()
                .key("id")
                .label("ID")
                //.hidden(true) // TODO: fix dataTable.doBeforeLoadData insertColumn issue
                .formatter(FormatterEnum.NUMBER.name().toLowerCase())
                .className(CssClasName.RIGHT)
                .build(),
            ColumnDef.builder()
                .key("name")
                .label("Name")
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
