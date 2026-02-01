package com.totemsoft.page.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonValue;
import com.totemsoft.page.model.ColumnDef.CellFormatterEnum;
import com.totemsoft.page.model.entity.Tag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Row {

    @JsonValue
    private Map<String, Cell<?>> cells;

    public static List<ColumnDef> columns(Set<Tag> columnTags) {
        final var columnDefs = new ArrayList<ColumnDef>();
        columnDefs.add(ColumnDef.builder()
            .key(ColumnDef.COLUMN_TAG)
            .label(CssClasName.NBSP)
            .formatter(ColumnDef.COLUMN_TAG.toLowerCase())
            .className(CssClasName.TAG)
            .build());
        columnTags.forEach(t -> columnDefs.add(ColumnDef.builder()
            .key(t.getName())
            .label(t.getTitle())
            .formatter(CellFormatterEnum.CURRENCY.name().toLowerCase())
            .className(CssClasName.RIGHT)
            .build()));
        return columnDefs;
    }

}
