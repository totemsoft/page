package com.totemsoft.page.model;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.totemsoft.page.model.ColumnDef.CellEditorEnum;
import com.totemsoft.page.model.ColumnDef.CellFormatterEnum;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(onlyExplicitlyIncluded = true)
public class KeyDto {

    @ToString.Include
    private Long id;

    @ToString.Include
    @NotBlank
    private String name;

    private String title;

    @JsonIgnore
    private List<TagDto> tags;

    public String getTagSummary() {
        return tags.stream()
            .map(t -> t.getTagTypeId() + ":" + t.getName())
            .collect(Collectors.joining(","));
    }

    public static List<ColumnDef> columns(boolean editable) {
        return List.of(
            ColumnDef.builder()
                .key("id")
                .label("ID")
                //.hidden(true) // TODO: fix dataTable.doBeforeLoadData insertColumn issue
                //.formatter(CellFormatterEnum.NUMBER.name().toLowerCase())
                //.className(CssClasName.RIGHT)
                .className("hidden")
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
                .build(),
            ColumnDef.builder()
                .key("tagSummary")
                .label("Tag Summary")
                .build(),
            ColumnDef.builder()
                .key("action")
                .label(CssClasName.NBSP)
                .className("collapsed")
                .build()
            );
    }

}
