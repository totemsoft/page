package com.totemsoft.page.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.totemsoft.page.model.ColumnDef.FormatterEnum;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SeriesDataDto {

    private Long id;

    //@JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private LocalDate date;

    private BigDecimal value;

    @NotBlank
    private String title;

    public static List<ColumnDef> columns() {
        return List.of(
            ColumnDef.builder()
                .key("id")
                .label("ID")
                //.hidden(true) // TODO: fix dataTable.doBeforeLoadData insertColumn issue
                .formatter(FormatterEnum.NUMBER.name().toLowerCase())
                .className(CssClasName.RIGHT)
                .build(),
            ColumnDef.builder()
                .key("date")
                .label("Date")
                //.formatter(FormatterEnum.DATE.name().toLowerCase())
                //.dateOptions("{format: '%d/%m/%Y', locale: 'en'}")
                .build(),
            ColumnDef.builder()
                .key("value")
                .label("Value")
                .formatter(FormatterEnum.CURRENCY.name().toLowerCase())
                //.currencyOptions("{}")
                .className(CssClasName.RIGHT)
                .build(),
            ColumnDef.builder()
                .key("title")
                .label("Name")
                .build()
            );
    }

}
