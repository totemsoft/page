package com.totemsoft.page.model;

import java.util.List;

import com.totemsoft.page.model.ColumnDef.CellEditorEnum;
import com.totemsoft.page.model.ColumnDef.CellFormatterEnum;
import com.totemsoft.page.model.ColumnDef.DropdownOption;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class TagDto {

    @EqualsAndHashCode.Include
    @ToString.Include
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

    public static List<ColumnDef> columnsTagsByKey(List<DropdownOption> dropdownOptions) {
        return List.of(
            ColumnDef.builder()
                .key("tagTypeId")
                .label("Tag Type")
                .formatter(CellFormatterEnum.DROPDOWN.name().toLowerCase())
                .dropdownOptions(dropdownOptions)
                //.editor(CellEditorEnum.DROPDOWN.name().toLowerCase())
                //.disableBtns(true)
                .build(),
            ColumnDef.builder()
                .key("name")
                .label("Name")
                .editor(CellEditorEnum.TEXTBOX.name().toLowerCase())
                .disableBtns(true)
                .sortable(true)
                .build()
            );
    }

}
