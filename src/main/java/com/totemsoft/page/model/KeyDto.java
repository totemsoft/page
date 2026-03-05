package com.totemsoft.page.model;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.totemsoft.page.model.ColumnDef.CellEditorEnum;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class KeyDto {

    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    @ToString.Include
    @NotBlank
    private String name;

    @ToString.Include
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
                .className(CssClasName.WIDTH0)
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
                .className(CssClasName.COLLAPSED)
                .width(25)
                .build()
            );
    }

}
